package org.wso2.carbon.user.core.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

/**
 * Tenant organization id cache which holds organization id as the key and tenant as the entry.
 */
public class TenantOrgIdCache {

    private static final String TENANT_ORG_ID_CACHE_MANAGER = "TENANT_ORG_ID_CACHE_MANAGER";
    private static final String TENANT_ORG_ID_CACHE = "TENANT_ORG_ID_CACHE";
    private static final Log log = LogFactory.getLog(TenantOrgIdCache.class);
    private static final TenantOrgIdCache tenantOrgIdCache = new TenantOrgIdCache();

    private TenantOrgIdCache() {

    }

    /**
     * Gets a new instance of {@link TenantOrgIdCache}.
     *
     * @return A new instance of {@link TenantOrgIdCache}.
     */
    public synchronized static TenantOrgIdCache getInstance() {

        return tenantOrgIdCache;
    }

    /**
     * Getting existing cache if the cache available, else returns a newly created cache.
     * This logic handles by javax.cache implementation
     */
    private <T> Cache<TenantOrgIdKey, T> getTenantUUIDCache() {

        CacheManager cacheManager = Caching.getCacheManagerFactory().getCacheManager(TENANT_ORG_ID_CACHE_MANAGER);
        return cacheManager.getCache(TENANT_ORG_ID_CACHE);
    }

    /**
     * Add a cache entry.
     * Tenant
     *
     * @param key   Key which cache entry is indexed.
     * @param entry Actual object where cache entry is placed.
     */
    public <T> void addToCache(TenantOrgIdKey key, T entry) {

        PrivilegedCarbonContext.startTenantFlow();

        try {
            startSuperTenantFlow();
            // Element already in the cache. Remove it first.
            clearCacheEntry(key);
            Cache<TenantOrgIdKey, T> cache = getTenantUUIDCache();
            if (cache != null) {
                cache.put(key, entry);
                if (log.isDebugEnabled()) {
                    log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER + ", " +
                            "added the entry : " + entry + " for the key : " + key + " successfully");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache : " + TENANT_ORG_ID_CACHE + " which is under " +
                            TENANT_ORG_ID_CACHE_MANAGER);
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Retrieves a cache entry.
     *
     * @param key CacheKey
     * @return Cached entry if the key presents, else returns null.
     */
    public <T> T getValueFromCache(TenantOrgIdKey key) {

        PrivilegedCarbonContext.startTenantFlow();

        try {
            startSuperTenantFlow();

            Cache<TenantOrgIdKey, T> cache = getTenantUUIDCache();
            if (cache != null) {
                if (cache.containsKey(key)) {
                    T entry = cache.get(key);
                    if (log.isDebugEnabled()) {
                        log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER +
                                ", found the entry : " + entry + " for the key : " + key + " successfully");
                    }
                    return entry;
                }
                if (log.isDebugEnabled()) {
                    log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER + ", " +
                            "doesn't contain the key : " + key);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache : " + TENANT_ORG_ID_CACHE + " which is under " +
                            TENANT_ORG_ID_CACHE_MANAGER);
                }
            }
            return null;
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Clears a cache entry.
     *
     * @param key Key to clear cache.
     */
    public void clearCacheEntry(TenantOrgIdKey key) {

        PrivilegedCarbonContext.startTenantFlow();

        try {
            startSuperTenantFlow();

            Cache<TenantOrgIdKey, Object> cache = getTenantUUIDCache();
            if (cache != null) {
                if (cache.containsKey(key)) {
                    cache.remove(key);
                    if (log.isDebugEnabled()) {
                        log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER + ", is "
                                + "removed entry for the key : " + key + " successfully");
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER + ", " +
                            "doesn't contain the key : " + key);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache : " + TENANT_ORG_ID_CACHE + " which is under " +
                            TENANT_ORG_ID_CACHE_MANAGER);
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private void startSuperTenantFlow() {

        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        carbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

    /**
     * Remove everything in the cache.
     */
    public void clear() {

        PrivilegedCarbonContext.startTenantFlow();

        try {
            startSuperTenantFlow();
            Cache<TenantOrgIdKey, Object> tenantUniqueIDCache = getTenantUUIDCache();
            if (tenantUniqueIDCache != null) {
                tenantUniqueIDCache.removeAll();
                if (log.isDebugEnabled()) {
                    log.debug(TENANT_ORG_ID_CACHE + " which is under " + TENANT_ORG_ID_CACHE_MANAGER + ", " +
                            "is cleared successfully");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache : " + TENANT_ORG_ID_CACHE + " which is under " +
                            TENANT_ORG_ID_CACHE_MANAGER);
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

}
