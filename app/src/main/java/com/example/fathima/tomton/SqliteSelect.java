package com.example.fathima.tomton;

import android.content.Context;
import android.content.SharedPreferences;

class SqliteSelect {
    protected Context ctx;
    private DatabaseHelper dbh;
    private SharedPreferences sp;
    SqliteSelect(Context context)
    {
        ctx=context;
        dbh=new DatabaseHelper(ctx);
        sp=ctx.getSharedPreferences("Login",0);
    }
    String[] selectSubject()
    {
        int priority=sp.getInt("priority",0);
        String branch=sp.getString("branch","");
        String designation=sp.getString("designation","");
        String subject=sp.getString("subject","");
        if(priority==3)
        {
            return subject.split(",");
        }
        if(priority==2)
        {
            String pos=designation.split(":")[0];
            if(pos.equalsIgnoreCase("TUTOR"))
            {
                int year=Integer.parseInt(designation.split(":")[1]);
                return dbh.getTutorSubject(year,branch);
            }
        }
        if(priority==1)
        {
            if(designation.equalsIgnoreCase("HOD"))
            {
                return dbh.getHodSubject(branch);
            }
        }
        return null;
    }

}
