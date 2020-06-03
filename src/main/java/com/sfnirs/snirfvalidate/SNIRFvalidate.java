/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfnirs.snirfvalidate;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import java.util.*;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;


/**
 *
 * @author Ted Huppert
 */



enum spec {
  Required, Optional, InValid;
}

class HDFinfo{
    public String name;
    public String fullpath;
    public String hdf5type;
    public String valuestr;
    public spec validity;
    public long[] dims;
    public HDFinfo(){
      
    }
    public HDFinfo(String Fullpath,String Name,String HDF5type,spec Validity){
        name=Name;
        fullpath=Fullpath;
        hdf5type=HDF5type;
        validity=Validity;
    }
    public void printinfo(){
        System.out.println(String.format("Name: %s  [%s]",name,hdf5type));
        if(null==validity){
            System.out.println("WARNING: UNKNOWN DATA ENTRY");
        }else switch (validity) {
            case Required:
                System.out.println("REQUIRED");
                break;
            case Optional:
                System.out.println("OPTIONAL");
                break;
            default:
                System.out.println("WARNING: UNKNOWN DATA ENTRY");
                break;
        }
        System.out.println(String.format("\t%s/%s",fullpath,name));
        System.out.println(String.format("\t NDIM=%d",dims.length));
        if(dims.length==1){
            System.out.println(String.format("\t [%d x 1]",dims[0]));
        }else{
           System.out.println(String.format("\t [%d x %d]",dims[0],dims[1]));
        }
        
    }
}

public class SNIRFvalidate {
    static String version = "1.0";
    static String builddate = "June 2nd 2020";
    
    
    static List<HDFinfo> HDFfields;
    static List<HDFinfo> Specfields;  

     public static void main(String[] args) {
        if(args.length==0 || args.length>0){
            if(args.length==0 || args[0].contains("-h")){
                // display the help info
                System.out.println("SNIRF Validator");
                System.out.println("written T. Huppert");
                System.out.println(String.format("Version: %s",version));
                System.out.println(String.format("Build data: %s",builddate));
                System.out.println("usage: SnirfValidate Simple.snirf <fileout>.txt >> saves to file");
                System.out.println("usage: SnirfValidate Simple.snirf >> displays to screen");
                System.out.println("usage: SnirfValidate -help >> displays this info");
                return;
            }
        }
        String fileIn = args[0];
        boolean isvalid = isvalid(fileIn);
        
        System.out.println("SNIRF Validator");
        System.out.println("written T. Huppert");
        System.out.println(String.format("Version: %s",version));
        System.out.println(String.format("Build data: %s",builddate));
        
        if(isvalid){
            System.out.println("Validation PASSED");
        }else{
            System.out.println("Validation FAILED");
        }
        int cnt=0;
        for(int i=0; i<HDFfields.size(); i++){
             HDFinfo info = HDFfields.get(i);
             if(info.validity==spec.InValid){
                info.printinfo(); 
                cnt++;
             }
        }
        if(cnt==0){
            System.out.println("no unknown datatypes found");
        }
        System.out.println("************************************");
        for(int i=0; i<HDFfields.size(); i++){
             HDFinfo info = HDFfields.get(i);
             info.printinfo(); 
        }        
    }
     
    static void SetSpecFields(String version){
        Specfields = new ArrayList<>();
        
      
                
        if("1.0".equals(version)){
        Specfields.add(new HDFinfo("/","formatVersion","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+","dataTimeSeries","valuearray",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+","time","valuevector",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+/measurementList[0-9]*","sourceIndex","value",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+/measurementList[0-9]*/","detectorIndex","value",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+/measurementList[0-9]*","wavelengthIndex","value",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/data[0-9]+/measurementList[0-9]*","dataType","value",spec.Required));
        
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","sourcePos2D","valuearray",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","detectorPos2D","valuearray",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","wavelengths","valuevector",spec.Required));

        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","SubjectID","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","MeasurementDate","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","MeasurementTime","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","LengthUnit","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","TimeUnit","string",spec.Required));
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags","FrequencyUnit","string",spec.Required));
        
        Specfields.add(new HDFinfo("/nirs[0-9]*/metaDataTags/","[a-z]+","string",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","wavelengthActual","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","wavelengthEmissionActual","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","wavelengthActual","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","dataTypeLabel","string",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","dataTypeIndex","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","sourcePower","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","detectorGain","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/measurementList[0-9]+","moduleIndex","value",spec.Optional));
        
        Specfields.add(new HDFinfo("/nirs[0-9]*/stim[0-9]+","name","string",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/stim[0-9]+","data","valuearray",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","wavelengthsEmission","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","sourcePos3D","valuearray",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","detectorPos3D","valuearray",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","frequencies","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","timeDelays","valuevector",spec.Optional));        
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","timeDelayWidths","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","momentOrder","valuevector",spec.Optional));        
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","correlationTimeDelays","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","correlationTimeDelayWidths","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","sourceLabels","stringvector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","detectorLabels","stringvector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","landmarkPos2D","valuearray",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","landmarkPos3D","valuearray",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","landmarkLabels","stringvector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/probe","useLocalIndex","value",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/aux[0-9]+","name","string",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/aux[0-9]+","dataTimeSeries","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/aux[0-9]+","time","valuevector",spec.Optional));
        Specfields.add(new HDFinfo("/nirs[0-9]*/aux[0-9]+","timeOffset","value",spec.Optional));
                      
        }
        
    } 
     
    static boolean isvalid(String filename){
        boolean flag = false;
        HDFfields = new ArrayList<>();
        
        
        try {
               int file_id =H5.H5Fopen(filename,HDF5Constants.H5F_ACC_RDONLY,HDF5Constants.H5P_DEFAULT);
               int gID =H5.H5Gopen(file_id,"/");
               String fullname ="";
               ScanInfo(gID,fullname);
               
               String Version="unknown";
               for(int i=0; i<HDFfields.size(); i++){
                   HDFinfo info = HDFfields.get(i);
                   if("formatVersion".equals(info.name)){
                       // TODO
                       Version="1.0";
                   }
               }
               SetSpecFields(Version);
               
               boolean[] foundreqs = new boolean[Specfields.size()];
               for(int j=0; j<Specfields.size();j++){
                    HDFinfo info2 = Specfields.get(j);
                    foundreqs[j] = info2.validity==spec.Optional;
               }
               
               for(int i=0; i<HDFfields.size(); i++){
                   HDFinfo info = HDFfields.get(i);
                   info.validity=spec.InValid;
                   for(int j=0; j<Specfields.size();j++){
                       HDFinfo info2 = Specfields.get(j);
                       
                       if(info.fullpath.matches(info2.fullpath) & 
                               info.name.matches(info2.name)){
                           info.validity=info2.validity;
                           foundreqs[j]=true;
                       }
                           
                       
                   }
               }
               
               for(int j=0; j<Specfields.size();j++){
                   flag = flag & foundreqs[j];
               }
               
               
        } catch (NullPointerException | HDF5LibraryException e) {
        }
        
        
        
        
        return flag;
    }  
    static void ScanInfo(int gID, String fullname){
        
        try{
            long[] nobj = new long[1];
            long MAX_NAME=1024;
            String[] groupName = new String[1];
            H5.H5Gget_num_objs(gID,nobj);
            H5.H5Iget_name(gID,groupName,MAX_NAME);
            for(int i=0; i<(int)nobj[0]; i++){
                String[] member_name = new String[1];
                H5.H5Gget_objname_by_idx(gID,i,member_name,MAX_NAME);
                int type = H5.H5Gget_objtype_by_idx(gID,i);
                if(type==0){ // group
                    int gID2 = H5.H5Gopen(gID,member_name[0]);
                    ScanInfo(gID2,String.format("%s/%s",fullname,member_name[0]));
                }else if(type==1){ // dataset
                    int dset = H5.H5Dopen(gID,member_name[0]);
                    int space = H5.H5Dget_space(gID);
                    int ndims = H5.H5Sget_simple_extent_ndims(gID);
                    int type2 = H5.H5Dget_type(gID);
                    String H5Ttype = String.format("%d", H5.H5Tget_class(type2));
                    
                    long[] dims = new long[ndims];
                    long[] MAX_DIMS = new long[ndims];
                    
                    H5.H5Sget_simple_extent_dims(gID, dims,MAX_DIMS);
                    
                    
                    HDFinfo thisinfo = new HDFinfo();
                    thisinfo.fullpath=fullname;
                    thisinfo.name=member_name[0];
                    thisinfo.hdf5type=H5Ttype;
                    thisinfo.dims = dims;
                    thisinfo.validity = spec.InValid;
                    HDFfields.add(thisinfo);
                }
                
                
            }
            
        } catch (NullPointerException | HDF5LibraryException e) {
        }
               
        
    }
    
        
     
}

