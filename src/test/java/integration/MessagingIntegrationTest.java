package integration;

public class MessagingIntegrationTest {
//
//    private static final int DB_INDEX = 1;
//    private static final long WAIT_TIME_SECONDS = 2;
//
//    private Bet bet1 = new Bet("user1", "match1", new Result("compId1", "team1", "team2", 3, 1), "bet1");
//    private Bet bet2 = new Bet("user2", "match2", new Result("compId1", "team3", "team9", 3, 3), "bet2");
//    private Bet bet3 = new Bet("user3", "match3", new Result("compId1", "team3", "team9", 3, 3), "bet3");
//
//    private List<Bet> bets = Lists.newArrayList(bet1, bet2, bet3);
//
//    private Jedis cache;
//
//    @BeforeClass
//    public static void setupOnce() throws Exception {
//
//        Given.environmentIsShutDown();
//        Given.environmentIsUpAndRunning(ApplicationConfig.class,
//                FakeDatabaseConfig.class,
//                MessagingConfig.class,
//                WebConfig.class);
//
//        TimeUnit.SECONDS.sleep(2);
//    }
//
//    @After
//    public void teardown() {
//        cache.flushAll();
//    }
//
//    @Test
//    public void testEndToEndMessaging() throws IOException, InterruptedException {
//        Jedis cache = ApplicationContextHolder.getBean(Jedis.class);
//
//        Channel senderChannel = ApplicationContextHolder.getBean(Channel.class);
//        Channel receiverChannel = ApplicationContextHolder.getBean(Channel.class);
//        TestCosumer testConsumer = new TestCosumer(receiverChannel);
//
//        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
//        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);
//
//        MongoCollection matchCollection = FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection();
//
//        FinalMatchResult result1 = new FinalMatchResult("match1", new Result("compId1", "team1", "team2", 1, 1));
//        FinalMatchResult result2 = new FinalMatchResult("match2", new Result("compId1", "team3", "team9", 3, 3));
//
//        BetsBatch betsBatch = new BetsBatch(3, bets, new MD5HashGenerator().getHash(bets));
//
//        cache.select(DB_INDEX);
//        cache.set("match1", new JsonUtils().toJson(result1.getResult()));
//        matchCollection.insertOne(Document.parse(new JsonUtils().toJson(result2)));
//
//        senderChannel.basicPublish(MessagingConstants.EXCHANGE_NAME, MessagingConstants.BETS_TO_SCORES_ROUTE, null, new JsonUtils().toJson(betsBatch).getBytes());
//
//        TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);
//
//        assertEquals("{\"payload\":[\"bet1\",\"bet2\"],\"type\":\"ACKNOWLEDGE_REQUEST\"}", testConsumer.getMessage());
//    }
//
//    private static class TestCosumer extends DefaultConsumer {
//
//        private String message;
//
//        public TestCosumer(Channel channel) {
//            super(channel);
//        }
//
//        @Override
//        public void handleDelivery(String consumerTag, Envelope envelope,
//                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
//            message = new String(body, "UTF-8");
//        }
//
//        public String getMessage() {
//            return message;
//        }
//    }

}
