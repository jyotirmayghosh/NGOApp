package com.example.jyotirmayghosh.ngo.permissions;

import java.util.ArrayList;

/**
 * Created by Krishnendu on 18-Feb-18.
 */

public interface PermissionRequestCallback {
    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}
