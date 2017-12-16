package com.hardskins.hardskins;

public class Site{
    private String site_name;
    private String site_address;
    private String site_photo_url;
    private String site_free_bonus_hour;
    private String site_free_bonus_hour_count;
    private String site_free_bonus_hour_hint;
    private String site_free_bonus_hour_time;
    private String site_free_bonus_link;
    private String site_free_bonus_reg;
    private String site_free_bonus_reg_count;
    private String site_ref_code;
    private String site_ref_link;




    public Site(){
         super();
    }



    public Site(String temp, String site_isnotify){
        site_name = temp;
        site_address = temp;
        site_photo_url = temp;
        site_free_bonus_hour = site_free_bonus_hour_time;
        site_free_bonus_hour_count = site_free_bonus_hour_time;
        site_free_bonus_hour_hint = site_free_bonus_hour_time;
        site_free_bonus_hour_time = temp;
        site_free_bonus_link = temp;
        site_free_bonus_reg = temp;
        site_free_bonus_reg_count = temp;
        site_ref_code = temp;
        site_ref_link = temp;

    }

    public void setName(String name) {
        site_name = name;
    }




    public String getAddress() {
        return site_address;
    }

    public void setAddress(String address) {
        site_address = address;
    }

    public String getNameImage() {
        return site_photo_url;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getSite_address() {
        return site_address;
    }

    public void setSite_address(String site_address) {
        this.site_address = site_address;
    }

    public String getSite_photo_url() {
        return site_photo_url;
    }

    public void setSite_photo_url(String site_photo_url) {
        this.site_photo_url = site_photo_url;
    }

    public String getSite_free_bonus_hour() {
        return site_free_bonus_hour;
    }

    public void setSite_free_bonus_hour(String site_free_bonus_hour) {
        this.site_free_bonus_hour = site_free_bonus_hour;
    }

    public String getSite_free_bonus_hour_count() {
        return site_free_bonus_hour_count;
    }

    public void setSite_free_bonus_hour_count(String site_free_bonus_hour_count) {
        this.site_free_bonus_hour_count = site_free_bonus_hour_count;
    }

    public String getSite_free_bonus_hour_hint() {
        return site_free_bonus_hour_hint;
    }

    public void setSite_free_bonus_hour_hint(String site_free_bonus_hour_hint) {
        this.site_free_bonus_hour_hint = site_free_bonus_hour_hint;
    }

    public String getSite_free_bonus_hour_time() {
        return site_free_bonus_hour_time;
    }

    public void setSite_free_bonus_hour_time(String site_free_bonus_hour_time) {
        this.site_free_bonus_hour_time = site_free_bonus_hour_time;
    }

    public String getSite_free_bonus_link() {
        return site_free_bonus_link;
    }

    public void setSite_free_bonus_link(String site_free_bonus_link) {
        this.site_free_bonus_link = site_free_bonus_link;
    }

    public String getSite_free_bonus_reg() {
        return site_free_bonus_reg;
    }

    public void setSite_free_bonus_reg(String site_free_bonus_reg) {
        this.site_free_bonus_reg = site_free_bonus_reg;
    }

    public String getSite_free_bonus_reg_count() {
        return site_free_bonus_reg_count;
    }

    public void setSite_free_bonus_reg_count(String site_free_bonus_reg_count) {
        this.site_free_bonus_reg_count = site_free_bonus_reg_count;
    }

    public String getSite_ref_code() {
        return site_ref_code;
    }

    public void setSite_ref_code(String site_ref_code) {
        this.site_ref_code = site_ref_code;
    }

    public String getSite_ref_link() {
        return site_ref_link;
    }

    public void setSite_ref_link(String site_ref_link) {
        this.site_ref_link = site_ref_link;
    }

    public String getName(){
        return site_name;
    }

//    public boolean getSite_isnotify() {
//        boolean site_notify = false;
//        if (Integer.parseInt(site_isnotify) == 1){
//            site_notify = true;
//        } else if (Integer.parseInt(site_isnotify) == 0){
//            site_notify = false;
//        }
//        return site_notify;
//    }

   




    public void setNameImage(String nameImage) {
        site_photo_url = nameImage;
    }


}