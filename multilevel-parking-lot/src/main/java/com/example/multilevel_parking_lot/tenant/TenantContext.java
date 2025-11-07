package com.example.multilevel_parking_lot.tenant;

/**
 * Simple ThreadLocal holder for current tenant id.
 * Set by TenantFilter and used by services/strategies.
 */
public final class TenantContext {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
