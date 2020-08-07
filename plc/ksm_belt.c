#include "rtapi.h"		/* RTAPI realtime OS API */
#include "rtapi_app.h"		/* RTAPI realtime module decls */
#include "rtapi_string.h"
#include "hal.h"		/* HAL public API decls */

#include <stdlib.h>

/* module information */
MODULE_AUTHOR("Sascha Ittner, Dominik Braun");
MODULE_DESCRIPTION("ksm belt component");
MODULE_LICENSE("GPL");

#define MAX_FIFO_COUNT	8

static int belt_length;
static int bucket_count;
static int station_count[MAX_FIFO_COUNT];
RTAPI_MP_INT(belt_length, "belt length in mm");
RTAPI_MP_INT(bucket_count, "number of bucket entries per fifo");
RTAPI_MP_ARRAY_INT(station_count, MAX_FIFO_COUNT, "number stations for each fifo");

static int fifo_count;

typedef struct {
	int index;

	// params
	hal_float_t *position;
	hal_float_t *on_offset;
	hal_float_t *off_offset;

	hal_float_t *marg_start;
	hal_float_t *marg_end;
	hal_float_t *marg_wo_extra;

	// inputs
	hal_bit_t *manu;
	hal_bit_t *enabled;
	hal_bit_t *force_fifo_en;

	// outputs
	hal_bit_t *activate_part;
	hal_bit_t *activate_full;
	hal_bit_t *out;
	hal_bit_t *out_inv;
	hal_float_t *height;

	// inout
 	hal_u32_t *whm_secs;
 	hal_u32_t *whm_dcms;

	// variables
	long whm_secs_fract;
	double whm_dcms_fract;
} station_data_t;

typedef struct {
	int index;

	station_data_t *stations;
	double *buckets;

	// params
	hal_float_t *probe_position;
	hal_float_t *probe_offset;
	hal_u32_t *probe_delay;

	// inputs
	hal_bit_t *enabled;
	hal_bit_t *material_switch;
	hal_bit_t *probe_enable;
	hal_float_t *probe_value;

	// outputs
	hal_bit_t *has_material;
	hal_float_t *probe_height;

	// variables
	int station_count;
	bool probe_enable_old;
	long long probe_delay_timer;
} fifo_data_t;

typedef struct {
	fifo_data_t *fifos;

	// params
	hal_float_t max_speed;
	hal_float_t mm_per_inc;
	hal_float_t speed_dt;
	hal_float_t speed_ovr_min;
	hal_float_t speed_ovr_max;
	hal_float_t speed_ovr_inc_per_sec;
	hal_float_t speed_matprep;

	// inputs
	hal_float_t *selected_height;
	hal_float_t *speed_cmd;
	hal_float_t *speed_ovr;
	hal_bit_t *automatic_running;
	hal_bit_t *reset_error;
 	hal_s32_t *enc_pos;
	hal_bit_t *amp_ready;
	hal_bit_t *ena_stations_out;
	hal_bit_t *speed_inc;
	hal_bit_t *speed_dec;
	hal_bit_t *calib_interlock;
	hal_bit_t *calib_matprep;
	hal_bit_t *whm_matswitch;
 	hal_bit_t *whm_job_reset;

	// outputs
	hal_float_t *speed;
	hal_bit_t *error;
	hal_bit_t *has_material;
 	hal_float_t *amp_speed;
	hal_bit_t *amp_enable;
	hal_float_t *enc_diff_mm;
 	hal_s32_t *bucket_diff;
 	hal_s32_t *bucket_pos;

	// inout
 	hal_u32_t *whm_secs;
 	hal_u32_t *whm_dcms;
 	hal_u32_t *whm_mdcm;
 	hal_u32_t *whm_job_secs;
 	hal_u32_t *whm_job_dcms;
 	hal_u32_t *whm_job_mdcm;

	// variables

	// variables
	bool initialized;
	bool enabled;
	double buckets_per_mm;
	int32_t enc_pos_old;
	double bucket_acc;
	long whm_secs_fract;
	double whm_dcms_fract;
	double whm_mdcm_fract;
} belt_data_t;


static int comp_id;

static void update(void *arg, long period);
static void update_fifo(belt_data_t *belt, fifo_data_t *fifo, long period);
static void update_station(belt_data_t *belt, fifo_data_t *fifo, station_data_t *station, long period);

static int export_belt(belt_data_t *data);
static int export_fifo(fifo_data_t *data, int index);
static int export_station(station_data_t *data, int fifo_index, int index);

int rtapi_app_main(void)
{
	int retval, i;
	belt_data_t *belt;
	fifo_data_t *fifo;
	int fifo_idx;
	station_data_t *station;
	int station_idx;

	// count fifos
	for (fifo_count = 0; fifo_count <= MAX_FIFO_COUNT; fifo_count++) {
		if (station_count[fifo_count] <= 0) {
			break;
		}
	}

	// check belt length
	if (belt_length <= 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "belt_length must be > 0\n");
		goto fail0;
	}

	// check bucket count
	if (bucket_count <= 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "bucket_count must be > 0\n");
		goto fail0;
	}

	// check fifo count
	if (fifo_count <= 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "fifo_count must be > 0\n");
		goto fail0;
	}

	// have good config info, connect to the HAL
	comp_id = hal_init("ksm_belt");
	if (comp_id < 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: hal_init() failed\n");
		goto fail0;
	}

	// alloc belt hal data
	belt = hal_malloc(sizeof(belt_data_t));
	if (belt == NULL) {
		rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: hal_malloc() for belt failed\n");
		goto fail1;
	}

	// export and initialize belt
	retval = export_belt(belt);
	if (retval != 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: belt export failed (error %d)\n", retval);
		goto fail1;
	}

	// alloc fifo hal data
	belt->fifos = hal_malloc(fifo_count * sizeof(fifo_data_t));
	if (belt->fifos == NULL) {
		rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: hal_malloc() for fifos failed\n");
		goto fail1;
	}

	// initialize fifos
	for (fifo_idx = 0, fifo = belt->fifos; fifo_idx < fifo_count; fifo_idx++, fifo++) {
		fifo->station_count = station_count[fifo_idx];

		// export and initialize fifo
		retval = export_fifo(fifo, fifo_idx);
		if (retval != 0) {
			rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: fifo %d export failed (error %d)\n", fifo_idx, retval);
			goto fail1;
		}

		// alloc stations hal data
		fifo->stations = hal_malloc(fifo->station_count * sizeof(station_data_t));
		if (fifo->stations == NULL) {
			rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: hal_malloc() for fifo stations failed\n");
			goto fail1;
		}

		// export and initialize stations
		for (station_idx = 0, station = fifo->stations; station_idx < fifo->station_count; station_idx++, station++) {
			retval = export_station(station, fifo_idx, station_idx);
			if (retval != 0) {
				rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: station %d.%d export failed (error %d)\n", fifo_idx, station_idx, retval);
				goto fail1;
			}
		}

		// alloc buckets hal data
		fifo->buckets = hal_malloc(bucket_count * sizeof(double));
		if (fifo->buckets == NULL) {
			rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: hal_malloc() for fifo buckets failed\n");
			goto fail1;
		}

		// initialize buckets
		for (i = 0; i < bucket_count; i++) {
			fifo->buckets[i] = 0.0;
		}
	}

	// export update function
	retval = hal_export_funct("ksm-belt", update, belt, 0, 0, comp_id);
	if (retval != 0) {
		rtapi_print_msg(RTAPI_MSG_ERR, "KSM_BELT: ERROR: belt function export failed (error %d)\n", retval);
		goto fail1;
	}

	rtapi_print_msg(RTAPI_MSG_INFO,	"KSM_BELT: installed %d fifos with bucket count of %d\n", fifo_count, bucket_count);
	hal_ready(comp_id);
	return 0;

fail1:
	hal_exit(comp_id);
fail0:
	return -1;
}

void rtapi_app_exit(void)
{
	hal_exit(comp_id);
}

static int32_t bucket_inc(int32_t pos, int32_t step)
{
	int32_t ret = pos + step;

	while (ret >= bucket_count) {
		ret -= bucket_count;
	}

	while (ret < 0) {
		ret += bucket_count;
	}

	return ret;
}

static void update(void *arg, long period)
{
	belt_data_t *belt = (belt_data_t *) arg;
	double period_sec = (double) period * 0.000000001;

	bool enabled;
	int i, j;
	int32_t enc_pos_diff;
	double speed_raw, enc_diff_bucket, speed_cmd;
	fifo_data_t *fifo;

	// initialize data
	if (!belt->initialized) {
		belt->buckets_per_mm = (double) belt_length / (double) bucket_count;
		belt->enc_pos_old = *(belt->enc_pos);
	}

	// reset buckets if get enabled
	enabled = *(belt->automatic_running) || *(belt->calib_interlock);
	if (!belt->initialized || (enabled && !belt->enabled)) {
		for (j = 0, fifo = belt->fifos; j < fifo_count; j++, fifo++) {
			for (i = 0; i < bucket_count; i++) {
				fifo->buckets[i] = 0.0;
			}
		}
	}

	belt->initialized = 1;
	belt->enabled = enabled;

	// get encoder diff
	enc_pos_diff = *(belt->enc_pos) - belt->enc_pos_old;
	belt->enc_pos_old = *(belt->enc_pos);

	// calculate diff in mm
	*(belt->enc_diff_mm) = (double) enc_pos_diff * belt->mm_per_inc;

	// claculate speed in mm/s
	speed_raw = *(belt->enc_diff_mm) / period_sec;

	// filter speed with damp time
	*(belt->speed) = *(belt->speed) + ((speed_raw - *(belt->speed)) * (period_sec / belt->speed_dt));

	// calculate diff in bucket steps
	enc_diff_bucket = *(belt->enc_diff_mm) * belt->buckets_per_mm;

	// get bucket steps to skip in this cacle
	belt->bucket_acc = belt->bucket_acc + enc_diff_bucket;
	*(belt->bucket_diff) = (int) belt->bucket_acc;
	belt->bucket_acc = belt->bucket_acc - (double) *(belt->bucket_diff);

	// fill required bucket steps
	for (i = 0; i < abs(*(belt->bucket_diff)); i++) {
		for (j = 0, fifo = belt->fifos; j < fifo_count; j++, fifo++) {
			// fill with selected material height if material is present, otherwise with zero
			if (*(fifo->material_switch)) {
				fifo->buckets[*(belt->bucket_pos)] = *(belt->selected_height);
			} else {
				fifo->buckets[*(belt->bucket_pos)] = 0;
			}
		}

		// calculate next bucket position
		if (*(belt->bucket_diff) > 0) {
			*(belt->bucket_pos) = bucket_inc(*(belt->bucket_pos), +1);
		} else {
			*(belt->bucket_pos) = bucket_inc(*(belt->bucket_pos), -1);
		}
	}

	// process speed override
	if (*(belt->automatic_running)) {
		if (*(belt->speed_inc)) {
			*(belt->speed_ovr) += belt->speed_ovr_inc_per_sec * period_sec;
		}
		if (*(belt->speed_dec)) {
			*(belt->speed_ovr) -= belt->speed_ovr_inc_per_sec * period_sec;
		}
	}
	if (*(belt->speed_ovr) > belt->speed_ovr_max) {
		*(belt->speed_ovr) = belt->speed_ovr_max;
	}
	if (*(belt->speed_ovr) < belt->speed_ovr_min) {
		*(belt->speed_ovr) = belt->speed_ovr_min;
	}

	// get speed cmd
	speed_cmd = 0.0;
	if (*(belt->calib_interlock)) {
		// handle calib interlock
		if (*(belt->calib_matprep)) {
			speed_cmd = belt->speed_matprep;
		}
	} else {
		// normal operation
		if (*(belt->automatic_running)) {
			speed_cmd = *(belt->speed_cmd) * *(belt->speed_ovr);
		}
	}

	if (speed_cmd < 0) {
		speed_cmd = 0;
	}
	if (speed_cmd > belt->max_speed) {
		speed_cmd = belt->max_speed;
	}

	// calculate amp output
	if (!belt->enabled || (speed_cmd == 0)) {
		*(belt->amp_speed) = 0.0;
		*(belt->amp_enable) = 0;
	} else {
		*(belt->amp_speed) = speed_cmd;
		*(belt->amp_enable) = 1;
	}

	// handle whm
	if (*(belt->whm_job_reset)) {
		*(belt->whm_job_secs) = 0;
		*(belt->whm_job_dcms) = 0;
		*(belt->whm_job_mdcm) = 0;
	}
	if (belt->enabled) {
		belt->whm_secs_fract += period;
		if (belt->whm_secs_fract >= 1000000000L) {
			belt->whm_secs_fract -= 1000000000L;
			(*(belt->whm_secs))++;
			(*(belt->whm_job_secs))++;
		}
		belt->whm_dcms_fract += *(belt->enc_diff_mm);
		if (belt->whm_dcms_fract >= 100.0) {
			belt->whm_dcms_fract -= 100.0;
			(*(belt->whm_dcms))++;
			(*(belt->whm_job_dcms))++;
		}
		if (*(belt->whm_matswitch)) {
			belt->whm_mdcm_fract += *(belt->enc_diff_mm);
			if (belt->whm_mdcm_fract >= 100.0) {
				belt->whm_mdcm_fract -= 100.0;
				(*(belt->whm_mdcm))++;
				(*(belt->whm_job_mdcm))++;
			}
		}
	}

	// handle error
	if (*(belt->amp_enable) && !*(belt->amp_ready)) {
		*(belt->error) = 1;
	} else if (*(belt->reset_error)) {
		*(belt->error) = 0;
	}

	// check for materiel
	*(belt->has_material) = 0;
	for (j = 0, fifo = belt->fifos; j < fifo_count; j++, fifo++) {
		*(fifo->has_material) = 0;
		for (i = 0; i < bucket_count; i++) {
			if (fifo->buckets[i] > 0.0) {
				*(belt->has_material) = 1;
				*(fifo->has_material) = 1;
			}
		}
	}

	// process stations
	for (j = 0, fifo = belt->fifos; j < fifo_count; j++, fifo++) {
		update_fifo(belt, fifo, period);
	}

}

static void update_fifo(belt_data_t *belt, fifo_data_t *fifo, long period)
{
	int i;
	station_data_t *station;
	int32_t pos;
	double val;
	bool probe_enable;

	// handle probe delay
	probe_enable = 0;
	if (*(fifo->probe_enable)) {
		if (fifo->probe_delay_timer > 0) {
			fifo->probe_delay_timer -= period;
		} else {
			probe_enable = 1;
		}
	} else {
		fifo->probe_delay_timer = *(fifo->probe_offset) * 10000000LL;
	}

	// fill in measured values (only process forward move)
	pos = bucket_inc(*(belt->bucket_pos), (int32_t) -(*(fifo->probe_position) * belt->buckets_per_mm));
	val = *(fifo->probe_value) + *(fifo->probe_offset);
	if (probe_enable) {
		if (fifo->probe_enable_old) {
			for (i = 0; i < *(belt->bucket_diff); i++, pos = bucket_inc(pos, -1)) {
				if (fifo->buckets[pos] > 0.0) {
					fifo->buckets[pos] = val;
				}
			}
		} else {
			for (i = 0; i < bucket_count && fifo->buckets[pos] > 0.0; i++, pos = bucket_inc(pos, -1)) {
				fifo->buckets[pos] = val;
			}
		}
		fifo->probe_enable_old = 1;
		*(fifo->probe_height) = val;
	} else {
		if (fifo->probe_enable_old) {
			for (i = 0; i < bucket_count && fifo->buckets[pos] > 0.0; i++, pos = bucket_inc(pos, +1)) {
				fifo->buckets[pos] = val;
			}
		}
		fifo->probe_enable_old = 0;
		*(fifo->probe_height) = 0.0;
	}

	// process stations
	for (i = 0, station = fifo->stations; i < fifo->station_count; i++, station++) {
		update_station(belt, fifo, station, period);
	}
}

static void update_station(belt_data_t *belt, fifo_data_t *fifo, station_data_t *station, long period)
{
	double on_pos, off_pos, tmp;
	bool use_activate_full, activate;
	int32_t bucket_on_pos, bucket_off_pos;
	double height;
	int height_count;

	// calculate on and off positions
	on_pos = *(station->position) - *(station->on_offset);
	off_pos = *(station->position) + *(station->off_offset);
	if (on_pos <= off_pos) {
		use_activate_full = 0;
		// add margin extra
		if (*(station->marg_start) <= 0) {
			on_pos = on_pos - *(station->marg_wo_extra);
		}
		if (*(station->marg_end) <= 0) {
			off_pos = off_pos + *(station->marg_wo_extra);
		}
	} else {
		use_activate_full = 1;
		tmp = on_pos;
		on_pos = off_pos;
		off_pos = tmp;
	}

	// calculate bucket positions
	bucket_on_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -(on_pos * belt->buckets_per_mm));
	bucket_off_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -(off_pos * belt->buckets_per_mm));

	// check for material
	height = 0.0;
	height_count = 0;
	activate = 0;
	*(station->activate_part) = 0;
	*(station->activate_full) = 1;
	bucket_on_pos = bucket_inc(bucket_on_pos, +1);
	while (bucket_on_pos != bucket_off_pos) {
		if (fifo->buckets[bucket_off_pos] > 0) {
			*(station->activate_part) = 1;
			height += fifo->buckets[bucket_off_pos];
			height_count++;
		} else {
			*(station->activate_full) = 0;
		}
			bucket_off_pos = bucket_inc(bucket_off_pos, +1);
	}

	if (use_activate_full) {
		activate = *(station->activate_full);
	} else {
		activate = *(station->activate_part);
	}

	// check margins
	if (*(station->marg_start) > 0) {
		bucket_on_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -(off_pos * belt->buckets_per_mm));
		bucket_off_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -((off_pos + *(station->marg_start)) * belt->buckets_per_mm));
		while (bucket_on_pos != bucket_off_pos) {
			if (fifo->buckets[bucket_off_pos] == 0) {
				activate = 0;
			}
			bucket_off_pos = bucket_inc(bucket_off_pos, +1);
		}
	}
	if (*(station->marg_end) > 0) {
		bucket_on_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -(on_pos * belt->buckets_per_mm));
		bucket_off_pos = bucket_inc(*(belt->bucket_pos), (int32_t) -((on_pos - *(station->marg_end)) * belt->buckets_per_mm));
		while (bucket_on_pos != bucket_off_pos) {
			if (fifo->buckets[bucket_on_pos] == 0) {
				activate = 0;
			}
			bucket_on_pos = bucket_inc(bucket_on_pos, +1);
		}
	}

	// check enable flags
	activate = activate && *(station->enabled) && (*(fifo->enabled) || *(station->force_fifo_en));

	// overwrite with manual mode, if not automatic running
	if (!belt->enabled) {
		activate = *(station->manu);
	}

	if (*(belt->ena_stations_out)) {
		*(station->out) = activate;
		*(station->out_inv) = !activate;
		if (activate) {
			station->whm_secs_fract += period;
			if (station->whm_secs_fract >= 1000000000L) {
				station->whm_secs_fract -= 1000000000L;
				(*(station->whm_secs))++;
			}
			station->whm_dcms_fract += *(belt->enc_diff_mm);
			if (station->whm_dcms_fract >= 100.0) {
				station->whm_dcms_fract -= 100.0;
				(*(station->whm_dcms))++;
			}
		}
	} else {
		*(station->out) = 0;
		*(station->out_inv) = 0;
	}

	// output material height
	if (height_count > 0) {
		*(station->height) = height / ((double) height_count);
	} else {
		*(station->height) = 0.0;
	}
}

static int export_belt(belt_data_t *data)
{
	int retval;

	retval = hal_param_float_newf(HAL_RW, &(data->max_speed), comp_id, "ksm-belt.max-speed");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->mm_per_inc), comp_id, "ksm-belt.mm-per-inc");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->speed_dt), comp_id, "ksm-belt.speed-dt");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->speed_ovr_min), comp_id, "ksm-belt.speed-ovr-min");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->speed_ovr_max), comp_id, "ksm-belt.speed-ovr-max");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->speed_ovr_inc_per_sec), comp_id, "ksm-belt.speed-ovr-inc-per-sec");
	if (retval != 0) { return retval; }
	retval = hal_param_float_newf(HAL_RW, &(data->speed_matprep), comp_id, "ksm-belt.speed-matprep");
	if (retval != 0) { return retval; }

	// export pins
	retval = hal_pin_float_newf(HAL_IN, &(data->selected_height), comp_id, "ksm-belt.selected-height");
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IN, &(data->speed_cmd), comp_id, "ksm-belt.speed-cmd");
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->speed_ovr), comp_id, "ksm-belt.speed-ovr");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->automatic_running), comp_id, "ksm-belt.automatic-running");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->reset_error), comp_id, "ksm-belt.reset-error");
	if (retval != 0) { return retval; }
	retval = hal_pin_s32_newf(HAL_IN, &(data->enc_pos), comp_id, "ksm-belt.enc-pos");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->amp_ready), comp_id, "ksm-belt.amp-ready");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->ena_stations_out), comp_id, "ksm-belt.ena-stations-out");
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->speed), comp_id, "ksm-belt.speed");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->error), comp_id, "ksm-belt.error");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->has_material), comp_id, "ksm-belt.has-material");
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->amp_speed), comp_id, "ksm-belt.amp-speed");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->amp_enable), comp_id, "ksm-belt.amp-enable");
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->enc_diff_mm), comp_id, "ksm-belt.enc-diff-mm");
	if (retval != 0) { return retval; }
	retval = hal_pin_s32_newf(HAL_OUT, &(data->bucket_diff), comp_id, "ksm-belt.bucket-diff");
	if (retval != 0) { return retval; }
	retval = hal_pin_s32_newf(HAL_OUT, &(data->bucket_pos), comp_id, "ksm-belt.bucket-pos");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_secs), comp_id, "ksm-belt.whm-secs");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_dcms), comp_id, "ksm-belt.whm-dcms");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_mdcm), comp_id, "ksm-belt.whm-mdcm");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->whm_matswitch), comp_id, "ksm-belt.whm-matswitch");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->whm_job_reset), comp_id, "ksm-belt.whm-job-reset");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_job_secs), comp_id, "ksm-belt.whm-job-secs");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_job_dcms), comp_id, "ksm-belt.whm-job-dcms");
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_job_mdcm), comp_id, "ksm-belt.whm-job-mdcm");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->speed_inc), comp_id, "ksm-belt.speed-inc");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->speed_dec), comp_id, "ksm-belt.speed-dec");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->calib_interlock), comp_id, "ksm-belt.calib-interlock");
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->calib_matprep), comp_id, "ksm-belt.calib-matprep");
	if (retval != 0) { return retval; }

	// initialize data
	data->max_speed = 0.0;
	data->mm_per_inc = 0.0;
	data->speed_dt = 0.0;
	data->speed_ovr_min = 0.5;
	data->speed_ovr_max = 2.0;
	data->speed_ovr_inc_per_sec = 0.1;
	data->speed_matprep = 10.0;

	*(data->automatic_running) = 0;
	*(data->selected_height) = 0.0;
	*(data->speed_cmd) = 0.0;
	*(data->speed_ovr) = 1.0;
	*(data->reset_error) = 0;
 	*(data->enc_pos) = 0;
	*(data->amp_ready) = 0;
	*(data->ena_stations_out) = 0;
	*(data->speed) = 0.0;
	*(data->error) = 0;
	*(data->has_material) = 0;
 	*(data->amp_speed) = 0.0;
	*(data->amp_enable) = 0;
	*(data->enc_diff_mm) = 0.0;
	*(data->bucket_diff) = 0;
	*(data->bucket_pos) = 0;
	*(data->whm_secs) = 0;
	*(data->whm_dcms) = 0;
	*(data->whm_mdcm) = 0;
	*(data->whm_job_reset) = 0;
	*(data->whm_job_secs) = 0;
	*(data->whm_job_dcms) = 0;
	*(data->whm_job_mdcm) = 0;
	*(data->speed_inc) = 0;
	*(data->speed_dec) = 0;
	*(data->calib_interlock) = 0;
	*(data->calib_matprep) = 0;

	data->enabled = 0;
	data->initialized = 0;
	data->enabled = 0;
	data->buckets_per_mm = 0.0;
	data->enc_pos_old = 0;
	data->bucket_acc = 0.0;
	data->whm_secs_fract = 0;
	data->whm_dcms_fract = 0.0;
	data->whm_mdcm_fract = 0.0;

	return 0;
}

static int export_fifo(fifo_data_t *data, int index)
{
	int retval;

	// export parameters
	retval = hal_pin_float_newf(HAL_IO, &(data->probe_position), comp_id, "ksm-belt.fi-%d.probe-position", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IO, &(data->probe_offset), comp_id, "ksm-belt.fi-%d.probe-offset", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->probe_delay), comp_id, "ksm-belt.fi-%d.probe-delay", index);
	if (retval != 0) { return retval; }

	// export pins
	retval = hal_pin_bit_newf(HAL_IN, &(data->enabled), comp_id, "ksm-belt.fi-%d.enabled", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->material_switch), comp_id, "ksm-belt.fi-%d.material-switch", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->probe_enable), comp_id, "ksm-belt.fi-%d.probe-enable", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IN, &(data->probe_value), comp_id, "ksm-belt.fi-%d.probe-value", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->probe_height), comp_id, "ksm-belt.fi-%d.probe-height", index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->has_material), comp_id, "ksm-belt.fi-%d.has-material", index);
	if (retval != 0) { return retval; }

	// initialize data
	data->index = index;
	data->probe_enable_old = 0;
	data->probe_delay_timer = 0;

	*(data->probe_position) = 0.0;
	*(data->probe_offset) = 0.0;
	*(data->probe_delay) = 0.0;

	*(data->enabled) = 0;
	*(data->material_switch) = 0;
	*(data->probe_enable) = 0;
	*(data->probe_value) = 0.0;
	*(data->probe_height) = 0.0;
	*(data->has_material) = 0;

	return 0;
}

static int export_station(station_data_t *data, int fifo_index, int index)
{
	int retval;

	// export parameters
	retval = hal_pin_float_newf(HAL_IO, &(data->position), comp_id, "ksm-belt.fi-%d.st-%d.position", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IO, &(data->on_offset), comp_id, "ksm-belt.fi-%d.st-%d.on-offset", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IO, &(data->off_offset), comp_id, "ksm-belt.fi-%d.st-%d.off-offset", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IN, &(data->marg_start), comp_id, "ksm-belt.fi-%d.st-%d.marg-start", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IN, &(data->marg_end), comp_id, "ksm-belt.fi-%d.st-%d.marg-end", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_IO, &(data->marg_wo_extra), comp_id, "ksm-belt.fi-%d.st-%d.marg-wo-extra", fifo_index, index);
	if (retval != 0) { return retval; }

	// export pins
	retval = hal_pin_bit_newf(HAL_IN, &(data->manu), comp_id, "ksm-belt.fi-%d.st-%d.manu", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->enabled), comp_id, "ksm-belt.fi-%d.st-%d.enabled", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_IN, &(data->force_fifo_en), comp_id, "ksm-belt.fi-%d.st-%d.force-fifo-en", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->activate_part), comp_id, "ksm-belt.fi-%d.st-%d.activate-part", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->activate_full), comp_id, "ksm-belt.fi-%d.st-%d.activate-full", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->out), comp_id, "ksm-belt.fi-%d.st-%d.out", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_bit_newf(HAL_OUT, &(data->out_inv), comp_id, "ksm-belt.fi-%d.st-%d.out-inv", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_secs), comp_id, "ksm-belt.fi-%d.st-%d.whm-secs", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_u32_newf(HAL_IO, &(data->whm_dcms), comp_id, "ksm-belt.fi-%d.st-%d.whm-dcms", fifo_index, index);
	if (retval != 0) { return retval; }
	retval = hal_pin_float_newf(HAL_OUT, &(data->height), comp_id, "ksm-belt.fi-%d.st-%d.height", fifo_index, index);
	if (retval != 0) { return retval; }

	data->index = index;

	*(data->position) = 0.0;
	*(data->on_offset) = 0.0;
	*(data->off_offset) = 0.0;
	*(data->marg_start) = 0.0;
	*(data->marg_end) = 0.0;
	*(data->marg_wo_extra) = 0.0;

	*(data->manu) = 0;
	*(data->enabled) = 0;
	*(data->force_fifo_en) = 0;
	*(data->activate_part) = 0;
	*(data->activate_full) = 0;
	*(data->out) = 0;
	*(data->out_inv) = 0;
	*(data->whm_secs) = 0;
	*(data->whm_dcms) = 0;
	*(data->height) = 0.0;

	data->whm_secs_fract = 0;
	data->whm_dcms_fract = 0.0;

	return 0;
}

