# Implementation Plan - Personal Information Form

This plan outlines the steps to replace the current calculator application with a personal information collection form as requested.

## User Review Required

> [!IMPORTANT]
> The application will be completely replaced. The current calculator logic will be removed.

## Proposed Changes

### UI Components

#### [MODIFY] [activity_main.xml](file:///D:/Android/projectAndroid/AndroidStudioProjects/MyApplicationDO/app/src/main/res/layout/activity_main.xml)
- Implement a form with:
    - Full Name (Họ tên) - EditText
    - ID Card (CCCD) - EditText (Numeric)
    - Degree (Bằng cấp) - RadioGroup (Trung cấp, Cao đẳng, Đại học)
    - Interests (Sở thích) - CheckBoxes (Đọc báo, Đọc sách, Đọc Coding)
    - Additional Information (Thông tin bổ sung) - EditText (Multiline)
    - Submit Button (Gửi thông tin)
- Wrap in a `ScrollView` for better accessibility.

### Application Logic

#### [MODIFY] [MainActivity.java](file:///D:/Android/projectAndroid/AndroidStudioProjects/MyApplicationDO/app/src/main/java/com/example/myapplicationdo/MainActivity.java)
- Bind all UI elements.
- Implement validation logic:
    - Name: Non-empty and at least 3 characters.
    - CCCD: Numeric and exactly 12 digits.
    - Degree: At least one selection.
- Implement "Gửi thông tin" button click:
    - Validate inputs.
    - Show `AlertDialog` with the summary of information if valid.
    - Show `Toast` for validation errors.

## Verification Plan

### Manual Verification
- Deploy to an Android device/emulator.
- Test validation by entering:
    - Name < 3 characters.
    - CCCD with non-digits or != 12 digits.
    - No degree selected.
- Verify that a valid submission shows the `AlertDialog` with the correct formatted text.
