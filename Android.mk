LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional eng

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := FPAlarmClock
LOCAL_OVERRIDES_PACKAGES := AlarmClock DeskClock
LOCAL_CERTIFICATE := platform

#LOCAL_PROGUARD_ENABLED := full
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
