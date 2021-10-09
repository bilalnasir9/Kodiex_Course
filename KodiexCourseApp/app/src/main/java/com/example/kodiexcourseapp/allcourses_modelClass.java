package com.example.kodiexcourseapp;

import java.util.ArrayList;
import java.util.List;

public class allcourses_modelClass {

    List<String> list_courses_keys = new ArrayList<>();
    List<String>list_curriculum_title=new ArrayList<>();
     List<String>list_curriculum_duration=new ArrayList<>();
     List<String>list_curriculum_link=new ArrayList<>();
    List<String>list_instructor=new ArrayList<>();
    List<String>list_lectures=new ArrayList<>();
    List<String>list_level=new ArrayList<>();
    List<String>list_price=new ArrayList<>();
    List<String>list_rating=new ArrayList<>();
    List<String>list_subject=new ArrayList<>();
    List<String>list_title=new ArrayList<>();
    List<String> list_image_url = new ArrayList<>();
    public allcourses_modelClass(List<String> list_image_url,List<String> list_courses_keys, List<String> list_curriculum_title, List<String> list_curriculum_duration, List<String> list_curriculum_link, List<String> list_instructor, List<String> list_lectures, List<String> list_level, List<String> list_price, List<String> list_rating, List<String> list_subject, List<String> list_title) {
        this.list_courses_keys = list_courses_keys;
        this.list_curriculum_title = list_curriculum_title;
        this.list_curriculum_duration = list_curriculum_duration;
        this.list_curriculum_link = list_curriculum_link;
        this.list_instructor = list_instructor;
        this.list_lectures = list_lectures;
        this.list_level = list_level;
        this.list_price = list_price;
        this.list_rating = list_rating;
        this.list_subject = list_subject;
        this.list_title = list_title;
        this.list_image_url=list_image_url;
    }

    public List<String> getList_image_url() {
        return list_image_url;
    }

    public List<String> getList_courses_keys() {
        return list_courses_keys;
    }

    public List<String> getList_instructor() {
        return list_instructor;
    }

    public List<String> getList_lectures() {
        return list_lectures;
    }

    public List<String> getList_level() {
        return list_level;
    }

    public List<String> getList_price() {
        return list_price;
    }

    public List<String> getList_rating() {
        return list_rating;
    }

    public List<String> getList_subject() {
        return list_subject;
    }

    public List<String> getList_title() {
        return list_title;
    }

    public List<String> getList_curriculum_title() {
        return list_curriculum_title;
    }

    public List<String> getList_curriculum_duration() {
        return list_curriculum_duration;
    }

    public List<String> getList_curriculum_link() {
        return list_curriculum_link;
    }
}
