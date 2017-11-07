
package com.example.pankaj.lock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureStroke;
import android.util.Log;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Doodle
{
    int check =0;
    String user_dir="/data/user/0/com.example.vinothramss.Sher_Lock/files/gesture_users/";
    final int REP_SIZE = 12;
    ArrayList<double[]> numericalRep = new ArrayList<double[]>();
    final double THRESHOLD = 0.001;
    final int TOLERANCE = 5;
    double[] means = new double[REP_SIZE];
    double[] variances = new double[REP_SIZE];



    int data_count=0;
    List<GestureEvents> mElist =   new ArrayList();
    ArrayList<Gesture> mGlist=new ArrayList();
    ArrayList<double[]> data = new ArrayList<double[]>();


    public Doodle(ArrayList<Gesture> gestureList, ArrayList<GestureEvents> list)
    {

        int p=gestureList.size();
        if(p>list.size())
            p=list.size();

        Log.d("MachineLearning",list.size()+":1size :"+gestureList.size());
        mGlist=gestureList;
        data_count=p;
        if(list.size()>0)
        mElist=list.subList(0,p);
        Log.d("MachineLearning",mElist.size()+":size :"+gestureList.size());
        for(GestureEvents g : mElist)
        {
           // Log.d("MachineLearning"," Moiton Event data:"+g.list.size());
        }

        for (int i=0;i<p;i++)
        {
            data.add(gestureToArray(mGlist.get(i),mElist.get(i),13,1));
        }


    }

    public void addNewDataDoodle(ArrayList<Gesture> gestureList, ArrayList<GestureEvents> list)
    {

        int p=gestureList.size();
        if(p>list.size())
            p=list.size();

        Log.d("MachineLearning",list.size()+":1size :"+gestureList.size());
        mGlist=gestureList;
        data_count=p;
        if(list.size()>0)
            mElist=list.subList(0,p);
        Log.d("MachineLearning",mElist.size()+":size :"+gestureList.size());
        for(GestureEvents g : mElist)
        {
            // Log.d("MachineLearning"," Moiton Event data:"+g.list.size());
        }

        for (int i=0;i<p;i++)
        {
            data.add(gestureToArray(mGlist.get(i),mElist.get(i),13,0));
        }


    }

    public boolean isFilePresent(String path)
    {
        File file = new File(path);
        return file.exists();
    }

    public boolean authenticate(Gesture testGesture,GestureEvents  testEvent,ArrayList<String> users)
    {
        check =1;
        list_file();
        Log.d("Machinelearning","Users LIST"+users.size());
        int i;
        for(i=0;i<users.size();i++)
        {
            File  mUserFile=new File(user_dir+users.get(i)+users.get(i));
            GestureLibrary userStore = GestureLibraries.fromFile(mUserFile);
            Log.d("Machinelearning","user Directory:"+mUserFile.getAbsolutePath());
            ArrayList<GestureEvents> mElist =   new ArrayList();
            String EventFile=mUserFile + "E";
            Log.d("Machinelearning","Event file:"+EventFile);

            try {

                if (isFilePresent(EventFile) == true)
                {
                    FileInputStream fis = new FileInputStream(new File(EventFile));
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    mElist = (ArrayList<GestureEvents>) ois.readObject();
                    Log.d("Machinelearning", "event Auth:" + mElist.size());
                    ois.close();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            userStore.load();

            ArrayList<Gesture> gesturesFromFile = new ArrayList<Gesture>();
            for (String entry : userStore.getGestureEntries())
            {
                gesturesFromFile.addAll(userStore.getGestures(entry));
            }
          addNewDataDoodle(gesturesFromFile,mElist);
        }

        double[] test =gestureToArray(testGesture,testEvent,12,1);
        double result= predict(data,test);
        Log.d("MachineLearning","Result :"+result);

        return true;
    }

    private void list_file()
    {
        File mUserDir=new File(user_dir);
        if(mUserDir != null)
        {
            for (File file : mUserDir.listFiles())
            {
                Log.d("machinelearning","\nFILE --> "+file.getName());
            }
        }
    }
    private double[] gestureToArray(Gesture gesture,  GestureEvents  event ,int size ,int clas)
    {
        double[] gestureValues = new double[size];
        ArrayList<GestureStroke> strokes = gesture.getStrokes();

        GestureStroke currentStroke = strokes.get(0);
        gestureValues[0] = currentStroke.length;
        gestureValues[1] = currentStroke.points[0];
        gestureValues[2] = currentStroke.points[1];
        gestureValues[3] = currentStroke.points[currentStroke.points.length - 2];
        gestureValues[4] = currentStroke.points[currentStroke.points.length - 1];
        gestureValues[5] = currentStroke.boundingBox.width();
        gestureValues[6] = currentStroke.boundingBox.height();
        long duration = 0;
        try
        {
            Field f = currentStroke.getClass().getDeclaredField("timestamps");
            f.setAccessible(true);
            long[] timestamps = (long[]) f.get(currentStroke);
            duration = timestamps[timestamps.length - 1]
                    - timestamps[0];
        } catch (Exception e) {
            Log.e("ERROR", "Reflection didn't work");
        }

      //  Log.e("machineLearning", "ERRORp"+event.list.size());
        gestureValues[7] = duration;
        gestureValues[8] = event.getStartx();
        gestureValues[9] = event.getStarty();
        gestureValues[10] = event.getVelocityx();
        gestureValues[11] = event.getVelocityy();




        if(check==1)
        {
            System.out.println("------------------------------------feature list----------------------------------------------");
            System.out.println("feature 0 -strokes Size   :" + strokes.size());
            System.out.println("feature 1 -length   :" + gesture.getLength());
            System.out.println("feature 2 -duration :" + duration);
            System.out.println("feature 3 -points   :" + currentStroke.points.length);
            System.out.println("feature 4 -boxw   :" + currentStroke.boundingBox.width());
            System.out.println("feature 5 -boxh    :" + currentStroke.boundingBox.height());
            System.out.println("feature 6 -points(x,y)  :" + currentStroke.points.length);

        }
        else
        {
            gestureValues[12] = clas;
        }
        return gestureValues;
    }

    private double predict( ArrayList<double[]> myValues, double[] test)
    {
        double classify=0.0;

        Log.d("MachineLearning","Training Size :"+myValues.size());

        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute("a"));
        attributes.add(new Attribute("b"));
        attributes.add(new Attribute("c"));
        attributes.add(new Attribute("d"));
        attributes.add(new Attribute("e"));
        attributes.add(new Attribute("f"));
        attributes.add(new Attribute("g"));
        attributes.add(new Attribute("h"));
        attributes.add(new Attribute("i"));
        attributes.add(new Attribute("j"));
        attributes.add(new Attribute("k"));
        attributes.add(new Attribute("l"));
        attributes.add(new Attribute("m"));


        ArrayList<String> classLabels = new ArrayList<String>();
        classLabels.add("1");
        classLabels.add("0");
        Attribute classAtt = new Attribute("class", classLabels);
        attributes.add(classAtt);

        Instances dataRaw = new Instances("TestInstances", attributes, 0);
        Log.d("MachineLearning","Atttribute SIZE: "+dataRaw.numAttributes());

        Log.d("MachineLearning","DATA SIZE: "+myValues.get(0).length);
        Log.d("MachineLearning","TEST DATA SIZE: "+test.length);

        for (double[] a : myValues)
        {

            Log.d("MachineLearning",a.length+"RAW DATA: "+a[0]+"--"+a[1]+"--"+a[2]+"--"+a[3]+"--"+a[4]+"--"+a[5]+"--"+a[6]+"--"+a[7]+"-->>>>>"+a[12]);
            dataRaw.add(new DenseInstance(1.0, a));
        }



        Log.d("MachineLearning","TRAIN :\n"+dataRaw);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        Log.d("MachineLearning","NUMBER OF CLASSES :\n"+dataRaw.numClasses());
        // Assuming z (z on lastindex) as classindex

        try {
            // Then train or build the algorithm/model on instances (dataRaw) created above.

            J48 mlp =   new J48();
            // Sample algorithm, go through about neural networks to use this or replace with appropriate algorithm.
            mlp.buildClassifier(dataRaw);

            // Create a test instance,I think you can create testinstance without
            // classindex value but cross check in weka as I forgot about it.

            double[] values = test;// sample values
            DenseInstance testInstance = new DenseInstance(1.0, values);
            testInstance.setDataset(dataRaw); // To associate with instances object

            // now you can clasify
            classify = mlp.classifyInstance(testInstance);

            return classify;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return  classify;
    }
}