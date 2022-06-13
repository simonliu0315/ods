package gov.sls.ods.config;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

import com.cht.commons.context.cache.EhcacheCachesProvider;

@Component
public class OdsEhcacheCachesProvider implements EhcacheCachesProvider {

    public List<Cache> getCaches() {
        List<Cache> caches = new LinkedList<Cache>();
        caches.add(createCache("odsResourceCache", 100, 6 * 60));
        return caches;
    }
    
    protected Cache createCache(String name, int maxEntriesLocalHeap, int timeToLiveSeconds) {
        CacheConfiguration cacheConfiguration = new CacheConfiguration(name, maxEntriesLocalHeap);
        cacheConfiguration.setEternal(false);
        // When the persistence strategy is "none", all cache stays in memory (with no overflow to
        // disk nor persistence on disk).
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.strategy(PersistenceConfiguration.Strategy.NONE);
        cacheConfiguration.setTimeToLiveSeconds(timeToLiveSeconds);
        Cache cache = new Cache(cacheConfiguration);
        return cache;
    }

}
