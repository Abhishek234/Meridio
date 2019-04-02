package com.example.tinder2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> a1;
    int i=0,x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        int a=(int)getIntent().getSerializableExtra("mapid");
        x=a;
        lvRss = (ListView) findViewById(R.id.lvRss);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url)
    {
        try
        {
            //openConnection() returns instance that represents a connection to the remote object referred to by the URL
            //getInputStream() returns a stream that reads from the open connection
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(NewsActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading" +"\n"+ "please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {


            while (i != 5)
            {
                try {
                    URL url = new URL("http://feeds.feedburner.com/ndtvcooks-latest");
                    URL url1 = new URL("http://feeds.feedburner.com/ndtvsports-latest");
                    URL url2 = new URL("http://feeds.feedburner.com/gadgets360-latest");
                    URL url3 = new URL("http://feeds.feedburner.com/ndtvprofit-latest");
                    URL url4 = new URL("http://feeds.feedburner.com/ndtvnews-people");


                    //creates new instance of PullParserFactory that can be used to create XML pull parsers
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    //Specifies whether the parser produced by this factory will provide support
                    //for XML namespaces
                    factory.setNamespaceAware(false);

                    //creates a new instance of a XML pull parser using the currently configured
                    //factory features
                    XmlPullParser xpp = factory.newPullParser();

                    // We will get the XML from an input stream
                    if (i == 0)
                        xpp.setInput(getInputStream(url), "UTF_8");
                    if (i == 1)
                        xpp.setInput(getInputStream(url1), "UTF_8");
                    if (i == 2)
                        xpp.setInput(getInputStream(url2), "UTF_8");
                    if (i == 3)
                        xpp.setInput(getInputStream(url3), "UTF_8");
                    if (i == 4)
                        xpp.setInput(getInputStream(url4), "UTF_8");

                    /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
                     * We should take into consideration that the rss feed name is also enclosed in a "<title>" tag.
                     * Every feed begins with these lines: "<channel><title>Feed_Name</title> etc."
                     * We should skip the "<title>" tag which is a child of "<channel>" tag,
                     * and take into consideration only the "<title>" tag which is a child of the "<item>" tag
                     *
                     * In order to achieve this, we will make use of a boolean variable called "insideItem".
                     */
                    boolean insideItem = false;

                    // Returns the type of current event: START_TAG, END_TAG, START_DOCUMENT, END_DOCUMENT etc..
                    int eventType = xpp.getEventType(); //loop control variable
                    int count=0;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        //if we are at a START_TAG (opening tag)
                        if (eventType == XmlPullParser.START_TAG) {
                            //if the tag is called "item"
                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            }
                            //if the tag is called "title"
                            else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    // extract the text between <title> and </title>

                                    titles.add(xpp.nextText());
                                    break;
                                    // Log.d("gaurav",titles.toString());
                                }
                            }
                            //if the tag is called "link"
                            else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem) {
                                    // extract the text between <link> and </link>
                                    links.add(xpp.nextText());
                                }
                            }
                        }
                        //if we are at an END_TAG and the END_TAG is called "item"
                        else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }
                        //
                        eventType = xpp.next(); //move to next element


                    }
                    a1 = new ArrayList<String>(titles);
                    Log.d("sur", titles.toString());

                } catch (MalformedURLException e) {
                    exception = e;
                } catch (XmlPullParserException e) {
                    exception = e;
                } catch (IOException e) {
                    exception = e;
                }
                i++;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            Intent intent = new Intent(NewsActivity.this, MainActivity.class);
            intent.putExtra("a1", a1);
            intent.putExtra("mapid",x);
            startActivity(intent);
            finish();
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
//
//            lvRss.setAdapter(adapter);


            progressDialog.dismiss();
        }
    }
}