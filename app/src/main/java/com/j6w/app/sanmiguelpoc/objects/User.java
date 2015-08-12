package com.j6w.app.sanmiguelpoc.objects;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ceosilvajr on 8/12/15.
 */
@Table(name = "Users")
public class User extends Model {

    @Column(name = "userId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long userId;
    @Column(name = "name")
    public String name;
    @Column(name = "birthday")
    public Date birthday;
    @Column(name = "gender")
    private String gender;
    @Column(name = "imagePath")
    private String imagePath;
    @Column(name = "createdDate")
    public Date createdDate;

    public User() {
        super();
    }

    public User(long userId, String name, String birthday, String gender, String imagePath) throws ParseException {
        super();
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.imagePath = imagePath;
        this.createdDate = new Date();

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        this.birthday = inputFormat.parse(birthday);

    }

    public String getHumanReadableTime() {
        return DateUtils.getRelativeTimeSpanString(createdDate.getTime(), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS).toString();
    }

    public static List<User> getHashtags() {
        return new Select().from(User.class).orderBy("createdDate DESC").execute();
    }

    public String getJsonObject(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
