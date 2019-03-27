# Copyright (C) 2018 The SyberiaOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/etc/permissions/privapp-permissions-miuicamera.xml:system/etc/permissions/privapp-permissions-miuicamera.xml \
    $(LOCAL_PATH)/etc/default-permissions/miuicamera-permissions.xml:system/etc/default-permissions/miuicamera-permissions.xml \
    $(LOCAL_PATH)/lib/libCameraEffectJNI.so:system/lib/libCameraEffectJNI.so \
    $(LOCAL_PATH)/lib/libblurbuster.so:system/lib/libblurbuster.so \
    $(LOCAL_PATH)/lib/libfiltergenerator.so:system/lib/libfiltergenerator.so \
    $(LOCAL_PATH)/lib/libjni_blurbuster.so:system/lib/libjni_blurbuster.so \
    $(LOCAL_PATH)/lib/libjni_chromaflash.so:system/lib/libjni_chromaflash.so \
    $(LOCAL_PATH)/lib/libjni_dualcamera.so:system/lib/libjni_dualcamera.so \
    $(LOCAL_PATH)/lib/libjni_filtergenerator.so:system/lib/libjni_filtergenerator.so \
    $(LOCAL_PATH)/lib/libjni_hazebuster.so:system/lib/libjni_hazebuster.so \
    $(LOCAL_PATH)/lib/libjni_makeupV2.so:system/lib/libjni_makeupV2.so \
    $(LOCAL_PATH)/lib/libjni_optizoom.so:system/lib/libjni_optizoom.so \
    $(LOCAL_PATH)/lib/libjni_seestraight.so:system/lib/libjni_seestraight.so \
    $(LOCAL_PATH)/lib/libjni_sharpshooter.so:system/lib/libjni_sharpshooter.so \
    $(LOCAL_PATH)/lib/libjni_stillmore.so:system/lib/libjni_stillmore.so \
    $(LOCAL_PATH)/lib/libjni_trueportrait.so:system/lib/libjni_trueportrait.so \
    $(LOCAL_PATH)/lib/libjni_truescanner_v2.so:system/lib/libjni_truescanner_v2.so \
    $(LOCAL_PATH)/lib/libjni_ubifocus.so:system/lib/libjni_ubifocus.so \
    $(LOCAL_PATH)/lib/libmorpho_group_portrait.so:system/lib/libmorpho_group_portrait.so \
    $(LOCAL_PATH)/lib/libmorpho_groupshot.so:system/lib/libmorpho_groupshot.so \
    $(LOCAL_PATH)/lib/libmorpho_memory_allocator.so:system/lib/libmorpho_memory_allocator.so \
    $(LOCAL_PATH)/lib/libmorpho_panorama.so:system/lib/libmorpho_panorama.so \
    $(LOCAL_PATH)/lib/libmorpho_panorama_gp.so:system/lib/libmorpho_panorama_gp.so \
    $(LOCAL_PATH)/lib/libseestraight.so:system/lib/libseestraight.so \
    $(LOCAL_PATH)/lib/libtruescanner.so:system/lib/libtruescanner.so \
    $(LOCAL_PATH)/lib64/libCameraEffectJNI.so:system/lib64/libCameraEffectJNI.so \
    $(LOCAL_PATH)/lib64/libmorpho_group_portrait.so:system/lib64/libmorpho_group_portrait.so \
    $(LOCAL_PATH)/lib64/libmorpho_groupshot.so:system/lib64/libmorpho_groupshot.so \
    $(LOCAL_PATH)/lib64/libmorpho_memory_allocator.so:system/lib64/libmorpho_memory_allocator.so \
    $(LOCAL_PATH)/lib64/libmorpho_panorama.so:system/lib64/libmorpho_panorama.so \
    $(LOCAL_PATH)/lib64/libmorpho_panorama_gp.so:system/lib64/libmorpho_panorama_gp.so \

PRODUCT_PACKAGES += \
    MiuiCamera
