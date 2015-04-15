package com.wemote.scorpio.modules.security.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

/**
 * 补充Shiro HasAnyPermissions的Tag.
 *
 * @author calvin
 */
public class HasAnyPermissionsTag extends PermissionTag {

    private static final long serialVersionUID = -4786931833148680306L;
    private static final String PERMISSION_NAMES_DELIMITER = ",";

    @Override
    protected boolean showTagBody(String permissionNames) {
        boolean hasAnyPermission = false;

        Subject subject = getSubject();

        if (subject != null) {
            // Iterate through permissions and check to see if the user has one of the permissions
            for (String permission : permissionNames.split(PERMISSION_NAMES_DELIMITER)) {

                if (subject.isPermitted(permission.trim())) {
                    hasAnyPermission = true;
                    break;
                }

            }
        }

        return hasAnyPermission;
    }

}
