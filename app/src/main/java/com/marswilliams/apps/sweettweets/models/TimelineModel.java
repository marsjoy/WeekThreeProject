package com.marswilliams.apps.sweettweets.models;

import com.marswilliams.apps.sweettweets.databases.SweetTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = SweetTweetsDatabase.class)
public class TimelineModel extends BaseModel {

	@PrimaryKey
	@Column
	Long id;

	// Define table fields
	@Column
	private String name;

	public TimelineModel() {
		super();
	}

	// Parse model from JSON
	public TimelineModel(JSONObject object){
		super();

		try {
			this.name = object.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Getters
	public String getName() {
		return name;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	/* The where class in this code below will be marked red until you first compile the project, since the code 
	 * for the SampleModel_Table class is generated at compile-time.
	 */
	
	// Record Finders
	public static TimelineModel byId(long id) {
		return new Select().from(TimelineModel.class).where(SampleModel_Table.id.eq(id)).querySingle();
	}

	public static List<TimelineModel> recentItems() {
		return new Select().from(TimelineModel.class).orderBy(SampleModel_Table.id, false).limit(300).queryList();
	}
}
