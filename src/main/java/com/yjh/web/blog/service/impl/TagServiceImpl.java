package com.yjh.web.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.yjh.common.exception.CommonErrorException;
import com.yjh.web.blog.domain.Blog;
import com.yjh.web.blog.domain.Tag;
import com.yjh.web.blog.mapper.TagMapper;
import com.yjh.web.blog.service.IBlogService;
import com.yjh.web.blog.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/4/20 16:48
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Autowired
    private IBlogService blogService;

    @Override
    public void saveTag(Tag tag) {
        // 查询标签名称是否已经存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        if (this.getOne(wrapper) != null) {
            throw new CommonErrorException("标签名称已经存在,不允许再添加");
        }
        this.save(tag);
    }

    @Override
    public Tag getTag(Long id) {
        return this.getById(id);
    }

    @Override
    public Tag getTagByName(String name) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name);
        return this.getOne(wrapper);
    }

    @Override
    public List<Tag> listTags(Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize());
        return this.list();
    }

    @Override
    public List<Tag> listTags() {
        List<Tag> list = this.list();
        list.forEach(item -> {
            List<Blog> blogs = blogService.listBlogsByTagId(item.getId());
            item.setBlogs(blogs);
        });
        return list;
    }

    @Override
    public List<Tag> listTags(Long blogId) {
        return baseMapper.listTags(blogId);
    }

    @Override
    public void updateTag(Tag tag) {
        // 查询该标签是否存在
        Tag tagOld = this.getById(tag.getId());
        if (tagOld == null) {
            throw new CommonErrorException("标签不存在,不允许更新");
        }
        if (tag.getName().equals(tagOld.getName())) {
            throw new CommonErrorException("标签名称已经存在,不允许更新");
        }
        this.updateById(tag);
    }

    @Override
    public void deleteTag(Long id) {
        this.removeById(id);
    }
}
