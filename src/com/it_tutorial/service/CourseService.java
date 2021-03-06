package com.it_tutorial.service;

import com.StoreEntities;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.it_tutorial.model.Course;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexandr Jelimalai
 */
public class CourseService {

    private final DatastoreService datastore;


    public CourseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public Collection<Course> getAll() {
        Query queryCourses = new Query(StoreEntities.COURSE).addSort(Course.DATE);
        List<Entity> courseEntities = datastore.prepare(queryCourses).asList(FetchOptions.Builder.withDefaults());
        return getCoursesFrom(courseEntities);
    }

    public Collection<Course> getByTutorial(String tutorialName) {
        Query query = new Query(StoreEntities.TUTORIAL).setFilter(new FilterPredicate(Course.NAME, FilterOperator.EQUAL, tutorialName));
        Entity tutorial = datastore.prepare(query).asSingleEntity();
        Query queryCourses = new Query(StoreEntities.COURSE).setAncestor(tutorial.getKey()).addSort(Course.DATE);
        List<Entity> courseEntities = datastore.prepare(queryCourses).asList(FetchOptions.Builder.withDefaults());
        return getCoursesFrom(courseEntities);
    }


    private List<Course> getCoursesFrom(List<Entity> courseEntities) {
        List<Course> courses = new ArrayList<>();
        for (Entity courseEntity : courseEntities) {
            courses.add(getCourseFrom(courseEntity));
        }
        return courses;
    }


    private Course getCourseFrom(Entity courseEntity) {
        Course course = new Course();
        course.setName(courseEntity.getProperty(Course.NAME).toString());
        course.setDescription(((Text) courseEntity.getProperty(Course.DESCRIPTION)).getValue());
        return course;
    }


    public Collection<Course> getCoursesWithCommonTutorial(String courseName) {
        Entity courseEntity = getEntityByName(courseName);
        Query queryCourses = new Query(StoreEntities.COURSE).setAncestor(courseEntity.getParent()).addSort(Course.DATE);
        List<Entity> courses = datastore.prepare(queryCourses).asList(FetchOptions.Builder.withDefaults());
        return getCoursesFrom(courses);
    }


    public Course getByName(String name) {
        Entity tutorialEntity = getEntityByName(name);
        return getCourseFrom(tutorialEntity);
    }


    private Entity getEntityByName(String name) {
        Query query = new Query(StoreEntities.COURSE).setFilter(new FilterPredicate(Course.NAME, FilterOperator.EQUAL, name));
        return datastore.prepare(query).asSingleEntity();
    }

}
