package com.niyiment.proccessor.utility;


public class ConstantUtility {

    public static final String PERSON_INSERT_QUERY = "INSERT INTO public.person (id, first_name, last_name, phone_number) " +
            "VALUES (:id, :firstName, :lastName, :phoneNumber)";
    public static final String PERSON_FETCH_QUERY = "SELECT id, first_name, last_name, phone_number FROM public.person";
    public static final String PERSON_FILENAME = "person";

    public static final String TEMP_BASE_DIR = System.getProperty("user.dir");
    public static final String TEMP_IMPORT_DIR = TEMP_BASE_DIR + "/import/";
    public static final String TEMP_EXPORT_DIR = TEMP_BASE_DIR + "/export/";


}
