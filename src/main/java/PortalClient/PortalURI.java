package PortalClient;

public class PortalURI {

    public static String PORTAL_URI_LAST_UPDATE_FOR_CLIENT = "/api/updatefiles/lastFile/";//  + {client} - (all,k,km,z,pm)
    public static String PORTAL_URI_LAST_UPDATE = "/api/updatefiles/lastFile";
    public static String PORTAL_URI_DOWNLOAD_UPDATE = "/api/updatefiles/downloadFile/"; // + {fileName}

    //only via :8080 port because this request without 'api' path
    public static String PORTAL_URI_DOWNLOAD_MATERIAL_IMAGE = "/content/"; // + {filePath}
}
