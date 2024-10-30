package com.qian.usercenter.service;

import com.qian.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @Resource
    private UserService userService;

   /* @Test
    void searchUsersByTags() {
        List<String> list = Arrays.asList("java","c++");
        List<User> userList = userService.searchUsersByTags(list);
        for (User user:userList
             ) {
            System.out.println(user.getUserAccount());
        }
    }*/
    class Student{
      private String name;
      private Long sal;
      private MyDate birthday;

       public Student(String name, Long sal, MyDate birthday) {
           this.name = name;
           this.sal = sal;
           this.birthday = birthday;
       }

       public String getName() {
           return name;
       }

       public void setName(String name) {
           this.name = name;
       }

       public Long getSal() {
           return sal;
       }

       public void setSal(Long sal) {
           this.sal = sal;
       }

       public MyDate getBirthday() {
           return birthday;
       }

       public void setBirthday(MyDate birthday) {
           this.birthday = birthday;
       }

       @Override
       public String toString() {
           return "Student{" +
                   "name='" + name + '\'' +
                   ", sal=" + sal +
                   ", birthday=" + birthday +
                   '}';
       }
   }
   class MyDate{
        private long year;
        private int moth;
        private int day;

       public MyDate(long year, int moth, int day) {
           this.year = year;
           this.moth = moth;
           this.day = day;
       }

       public long getYear() {
           return year;
       }

       public void setYear(long year) {
           this.year = year;
       }

       public int getMoth() {
           return moth;
       }

       public void setMoth(int moth) {
           this.moth = moth;
       }

       public int getDay() {
           return day;
       }

       public void setDay(int day) {
           this.day = day;
       }

       @Override
       public String toString() {
           return "MyDate{" +
                   "year=" + year +
                   ", moth=" + moth +
                   ", day=" + day +
                   '}';
       }
   }
   @Test
    void test() {
       ArrayList<Student> students = new ArrayList<>();
       students.add(new Student("limign",1L,new MyDate(2021l,3,18)));
       students.add(new Student("limign",1L,new MyDate(2021l,3,18)));
       students.add(new Student("limign",1L,new MyDate(2021l,3,18)));
       for (Student s:students
            ) {
           System.out.println(s);
       }
   }
}