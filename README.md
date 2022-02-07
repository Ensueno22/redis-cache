# RedisCache

### Configuration 설정
* GenericJackson2JsonRedisSerializer를 많이 써서 Class Type을 지정해 줄 필요없이 Object를 Json으로 직렬화 해준다 
* 그러나 Object의 Class Type을 함께 Redis 넣기 때문에 치명적인 단점이 있다.
* dto의 package 까지 저장되어 버리기 때문에 무조건 같은 package 루트로 Object를 생성해야하기 때문 해당 문제를 피하기 위해 StringRedisSerializer를 사용
``` Java
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfiguration = new HashMap<>();
        cacheConfiguration.put(RedisCacheKey.APP_KEY, redisCacheConfiguration.entryTtl(Duration.ofSeconds(180L)));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfiguration)
                .build();
    }
}
```

### RedisService Class 구현
* 누군가의 블로그에서 참조한 부분이다. RedisService를 만들어 Redis에 입력 및 조회를 가능하도록 생성된 클래스이다.
* 다만 단점은 springboot-start-web이 dependency 되어 있지 않으면 jackson-data-binder를 add하고 ObjectMapper를 bean으로 등록해 주어야 한다.
``` Java
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T getRedisValue(String key, Class<T> classType) throws JsonProcessingException {
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if(ObjectUtils.isEmpty(redisValue)){
            return null;
        } else {
            return objectMapper.readValue(redisValue, classType);
        }
    }

    public void putRedis(String key, Object classType) throws JsonProcessingException {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(classType));
    }
}
```

### Cache 사용 코드
``` Java
    @Cacheable(cacheNames = "findAppInfo", key = "#appKey", cacheManager = "redisCacheManager")
    public AppData findByAppKey(String appKey) throws JsonProcessingException {
        log.info("cached appKey={}", appKey);
        return redisService.getRedisValue(appKey, AppData.class);
    }
```
