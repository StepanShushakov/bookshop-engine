package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.TagEntity;

/**
 * @author karl
 */

public class TagCountDto {
    private String name;
    private Integer count;
    private String tagClass;

    public TagCountDto(TagEntity tagEntity) {
        this.name = tagEntity.getName();
        this.count = tagEntity.getBooks().size();
        if (this.count < 15) {
            this.tagClass = "Tag Tag_xs";
        } else if (this.count < 21) {
            this.tagClass = "Tag Tag_sm";
        } else if (this.count < 24) {
            this.tagClass = "Tag";
        } else if (this.count < 27) {
            this.tagClass = "Tag Tag_md";
        } else {
            this.tagClass = "Tag Tag_lg";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTagClass() {
        return tagClass;
    }

    public void setTagClass(String tagClass) {
        this.tagClass = tagClass;
    }
}
