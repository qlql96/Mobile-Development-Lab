package com.qilong.appletop25apprssfeed;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    private ArrayList<AppEntry> entries;

    public ParseApplications() {
       this.entries = new ArrayList<>();
    }

    public ArrayList<AppEntry> getEntries() {
        return this.entries;
    }

    public boolean parse (String xml){
        boolean status = true;
        AppEntry entry = null;
        boolean inEntry = false;
        String textValue = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            entry = new AppEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: ending tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                entries.add(entry);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                entry.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                entry.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                entry.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                entry.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                entry.setImageURL(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xpp.next();
            }
            for(AppEntry app: entries){
                Log.d(TAG,app.toString());
            }

        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
