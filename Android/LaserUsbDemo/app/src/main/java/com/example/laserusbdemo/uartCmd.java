package com.example.laserusbdemo;

public interface uartCmd {
    final String enter_debug_mode = "CD050106";
    final String get_time = "CD1110010628";
    final String get_tempture = "CD0A111B";
    final String get_single_distance = "CD010506";
    final String get_multi_distance = "CD010607";

    final String lase_on = "CD010304";
    final String lase_off = "CD010405";
    final String get_version = "CD010203";

    final String zero_cal = "CD0A000A";

    final String enable_distance_function = "CD0A232D";
    final String disable_distance_function = "CD0A242E";

    final String laser_grade_2 = "CD1106000219";
    final String laser_grade_3 = "CD110600031A";

    final String enable_phase_correction = "CD1107000119";
    final String disable_phase_correction = "CD1107000018";

    final String save_env = "CD0A030D";
}
