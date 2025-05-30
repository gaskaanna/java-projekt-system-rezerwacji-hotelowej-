package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for role-based access control on endpoints.
 * This annotation can be applied to controller methods to restrict access based on user roles.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleBasedAccess {
    
    /**
     * Specifies the roles that are allowed to access the endpoint.
     * If the user has any of these roles, access will be granted.
     * If no roles are specified, the aspect will check if the user is the owner of the resource.
     */
    RoleName[] allowedRoles() default {};
    
    /**
     * Specifies whether the user must be the owner of the resource to access it.
     * This is only applicable for USER role. ADMIN and MANAGER roles bypass this check.
     */
    boolean checkOwnership() default false;
    
    /**
     * Specifies the type of resource being accessed.
     * This is used to determine how to check ownership.
     */
    ResourceType resourceType() default ResourceType.RESERVATION;
    
    /**
     * Specifies the operations that are allowed on the resource.
     */
    Operation[] allowedOperations() default {};
    
    /**
     * Enum for the type of resource being accessed.
     */
    enum ResourceType {
        RESERVATION,
        ROOM
    }
    
    /**
     * Enum for the operations that can be performed on a resource.
     */
    enum Operation {
        VIEW,
        CREATE,
        UPDATE,
        DELETE,
        CONFIRM,
        CANCEL
    }
}