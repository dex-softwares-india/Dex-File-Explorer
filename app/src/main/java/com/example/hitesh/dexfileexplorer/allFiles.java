package com.example.hitesh.dexfileexplorer;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class allFiles extends Fragment {

    CircleDisplay circleDisplay;
    long mem=0;
    long total=0;
    List<fileItem> fileItemList;
    List<fileItem> itemList;
    ListView listView;
    files_adapter adapter;
    TextView path;
    String TAG="permission";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private File currentDir;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public allFiles() {

    }

    public static allFiles newInstance(String param1, String param2) {
        allFiles fragment = new allFiles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleDisplay=view.findViewById(R.id.circleDisplay);
        circleDisplay.setColor(Color.parseColor("#f442bf"));
        circleDisplay.setTextSize(10f);
        circleDisplay.setAnimDuration(3000);
        listView=view.findViewById(R.id.view_files);
        path=view.findViewById(R.id.path);
        if(isStoragePermissionGranted()) {
            getInfo();

        }

        //circleDisplay.showValue(75f,100f,true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_files, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("hmm","entered");
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            getInfo();
        }
    }

    public void getInfo(){
        if (currentDir==null)
        currentDir=new File(Environment.getExternalStorageDirectory().getPath());
        path.setText(currentDir.getAbsolutePath());
        fileItemList=new ArrayList<>();
        itemList=new ArrayList<>();
        adapter=new files_adapter(getContext(),fileItemList);
        listView.setAdapter(adapter);
        if (currentDir.exists())
            Log.i("heheh", "yup");
        File[] dirs = currentDir.listFiles();
        for (File f : dirs) {
            Date lastModDate=new Date(f.lastModified());
            DateFormat format=DateFormat.getDateTimeInstance();
            String date_modify=format.format(lastModDate);

            if (f.isDirectory()){
                File[] fbuf=f.listFiles();
                int buf=0;
                if (fbuf!=null){
                    buf = fbuf.length;
                }
                else
                    buf=0;
                String numItem=String.valueOf(buf)+" items";
                int count=fileItemList.size();
                fileItemList.add(count,new fileItem(f.getName(),f.getAbsolutePath(),date_modify,numItem,"files",""));
                Uri uri=Uri.fromFile(f);
                String type=getContext().getContentResolver().getType(uri);
                fileItemList.get(count).setType(type);

            }

            else {
                int count=itemList.size();
                itemList.add(count,new fileItem(f.getName(),f.getAbsolutePath(),date_modify,f.length()+" Byte","files2",""));
                Uri uri=Uri.fromFile(f);
                //String type=getContext().getContentResolver().getType(uri);
                //itemList.get(count).setType(type);
                String mime = "*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(
                        mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                itemList.get(count).setType(mime);
                Log.i("hmm",mime);

            }
            List<fileItem> newList=new ArrayList<>();
            newList.addAll(fileItemList);
            newList.addAll(itemList);
            adapter=new files_adapter(getContext(),newList);
            listView.setAdapter(adapter);
        }
        mem=currentDir.getFreeSpace();
        total=currentDir.getTotalSpace();
        float per=((float)mem/(float)total)*100;
        System.out.println(per);
        circleDisplay.showValue(per,100f,true);
        final TextView textView=getView().findViewById(R.id.textView3);
        float mem1=(((((float)mem)/1024f)/1024f)/1024f);
        float total1=(((((float)total)/1024f)/1024f)/1024f);
        textView.setText(Integer.toString((int)mem1)+"GB /"+Integer.toString((int)total1)+" GB");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fileItem item= (fileItem) adapter.getItem(i);
                if (item.getImage().equalsIgnoreCase("files")){
                    currentDir=new File(item.getPath());
                    getInfo();
                }
                else {
                    File file = new File(item.getPath());

                    Uri uri =  Uri.fromFile(file);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    String mime = "*/*";
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    if (mimeTypeMap.hasExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                        mime = mimeTypeMap.getMimeTypeFromExtension(
                                mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                    intent.setDataAndType(uri,mime);
                    Log.i("hmm",mime);

                    startActivity(intent);
                }
            }
        });

    }
    public void onBackPressed(){
        Log.i("back","pressed");
        try {
            currentDir=new File(currentDir.getParent());
            getInfo();
        }
        catch (Exception e){
            e.printStackTrace();
            getActivity().finish();
        }
    }
}
