package com.saif.wosafe.data;

public class Data {
    String userProfile,notificationType,notificationText,notificationTime;

    public Data(String userProfile, String notificationType, String notificationText, String notificationTime) {
        this.userProfile = userProfile;
        this.notificationType = notificationType;
        this.notificationText = notificationText;
        this.notificationTime = notificationTime;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }
}
