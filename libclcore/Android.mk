LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libclcore_neon_hax
LOCAL_SRC_FILES := libclcore_neon.bc
LOCAL_MODULE_SUFFIX := .bc
LOCAL_MODULE_CLASS := SHARED_LIBRARY
LOCAL_PRELINK_MODULE := true
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib
LOCAL_POST_INSTALL_CMD := $(hide) mv $(TARGET_OUT)/lib/$(LOCAL_MODULE)$(LOCAL_MODULE_SUFFIX) \
			  $(TARGET_OUT)/lib/libclcore_neon.bc
include $(BUILD_PREBUILT)

LOCAL_PATH :=
include $(CLEAR_VARS)
LOCAL_MODULE := libclcore_hax
LOCAL_SRC_FILES := ./prebuilts/sdk/renderscript/lib/arm64/libclcore.bc
LOCAL_MODULE_SUFFIX := .bc
LOCAL_MODULE_CLASS := SHARED_LIBRARY
LOCAL_PRELINK_MODULE := true
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib64
LOCAL_POST_INSTALL_CMD := $(hide) mv $(TARGET_OUT)/lib64/$(LOCAL_MODULE)$(LOCAL_MODULE_SUFFIX) \
			  $(TARGET_OUT)/lib64/libclcore.bc
include $(BUILD_PREBUILT)
