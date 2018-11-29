package lucene;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.Version;

/**
 * Created by root on 18-3-22.
 */
public class ConcurrentTest {
    private static final String PATH = "/home/song/someDemo/index";
    private static Analyzer zh = new ComplexAnalyzer();
    private static ReferenceManager<IndexSearcher> searcherManager;
    private static TrackingIndexWriter trackingIndexWriter;

    public static void main(String[] args) {
        try {
            final IndexWriter indexWriter = getIndexWriter();
            trackingIndexWriter = new TrackingIndexWriter(indexWriter);
            searcherManager = new SearcherManager(indexWriter, true, null);

            startDemonThread();

            long start=System.currentTimeMillis();
            ExecutorService ex= Executors.newFixedThreadPool(210);
            for(int i=0;i<10;i++){
                ex.execute(new Writer(trackingIndexWriter.getIndexWriter()));
            }
            Thread.sleep(99L);
            searcherManager.maybeRefresh();
            IndexSearcher searcher=searcherManager.acquire();
            //实时性验证
            for(int i=0;i<200;i++){
                //如果全部写完 666匹配的记录有10000条
                //结果不是1000的整数倍 说明commit()前 就读到了数据
                ex.execute(new Searcher(searcher,"666"));
                //等待索引更新一部分 希望 search能看到渐进的增长匹配数量
                //但是 使用同一个searcher似乎看到的结果都是一样的
                Thread.sleep(10);
            }
            searcherManager.release(searcher);

            ex.shutdown();
            ex.awaitTermination(1, TimeUnit.DAYS);
            System.out.println("用时："+(System.currentTimeMillis()-start));
            trackingIndexWriter.getIndexWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void startDemonThread(){
        //=========================================================
        // This thread handles the actual reader reopening.
        //=========================================================

        ControlledRealTimeReopenThread<IndexSearcher> nrtReopenThread = new ControlledRealTimeReopenThread<IndexSearcher>(
                trackingIndexWriter, searcherManager, 1.0, 0.1);
        nrtReopenThread.setName("NRT Reopen Thread");
        nrtReopenThread.setPriority(Math.min(Thread.currentThread().getPriority() + 2, Thread.MAX_PRIORITY));
        nrtReopenThread.setDaemon(true);
        nrtReopenThread.start();
    }

    static class Writer implements Runnable {
        IndexWriter writer;

        public Writer(IndexWriter writer) {
            this.writer = writer;
        }
        @Override
        public void run() {
            try {
                writer.addDocuments(getDocs(500));
                Thread.sleep(1000L);
                writer.addDocuments(getDocs(500));
                writer.commit();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Searcher implements Runnable {
        IndexSearcher searcher;
        String query;

        public Searcher(IndexSearcher searcher, String query) {
            this.searcher = searcher;
            this.query = query;
        }
        @Override
        public void run() {
            try {
                BooleanQuery bq = new BooleanQuery();
                Query q = new TermQuery(new Term("id", query));
                Query qName = new QueryParser(Version.LUCENE_45, "name", zh).parse(query);
                Query qC = new QueryParser(Version.LUCENE_45, "content", zh).parse(query);
                bq.add(q, BooleanClause.Occur.SHOULD);
                bq.add(qC, BooleanClause.Occur.SHOULD);
                bq.add(qName, BooleanClause.Occur.SHOULD);
                bq.setMinimumNumberShouldMatch(1);

                TopDocs docs = searcher.search(bq, 10);
//                System.out.println("Found " + docs.totalHits + " docs ");
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static void query(String word) throws IOException, ParseException {

        IndexSearcher searcher = searcherManager.acquire();
        BooleanQuery bq = new BooleanQuery();
        Query q = new TermQuery(new Term("id", word));
        Query qName = new QueryParser(Version.LUCENE_45, "name", zh).parse(word);
        Query qC = new QueryParser(Version.LUCENE_45, "content", zh).parse(word);
        bq.add(q, BooleanClause.Occur.SHOULD);
        bq.add(qC, BooleanClause.Occur.SHOULD);
        bq.add(qName, BooleanClause.Occur.SHOULD);
        bq.setMinimumNumberShouldMatch(1);

        TopDocs docs = searcher.search(bq, 10);
        System.out.println("Found " + docs.totalHits + " docs ");
        int loop=Math.min(10,docs.totalHits);
        for (int i = 0; i < loop; i++) {
            int id = docs.scoreDocs[i].doc;
            Document result = searcher.doc(id);
            System.out.println("id:" + result.get("id") + " name:" + result.get("name") + " content:" + result.get("content"));
        }

        searcherManager.release(searcher);

    }
    public void deleteDoc(Integer id) throws IOException {
        TermQuery q = new TermQuery(new Term("id", id.toString()));
//        TrackingIndexWriter writer = new TrackingIndexWriter(getIndexWriter());
        trackingIndexWriter.deleteDocuments(q);
        trackingIndexWriter.getIndexWriter().commit();
    }

    private static List<Document> getDocs(int number) {
        String[] emails = {"aa@itat.org", "bb@itat.org", "cc@cc.org", "dd@sina.org", "ee@zttc.edu", "ff@itat.org"};
        String[] contents = {
                "welcome to visited the space根据以上要求研发和运维团队也都提出了自己的架构方案",
                "hello boy, 使用hadoop做lucene的分布式索引",
                "分布式server集群，公用同一个网络磁盘作为索引目录 game",
                "I like  做一个索引同步的中间件",
                "有钱的可以选择方案 like basketball too",
                "方案三架构如下movie and swim"
        };
        List<Document> documentList = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            Document d = new Document();
            //不分词
            d.add(new StringField("name", emails[i % 6], Field.Store.YES));
            d.add(new StringField("id", 666 + "", Field.Store.YES));
            Date t = new Date();
            d.add(new LongField("time", t.getTime(), Field.Store.YES));
            //分词
            d.add(new TextField("content", contents[i % 6], Field.Store.YES));
            documentList.add(d);
        }
        return documentList;
    }

    private static IndexWriter getIndexWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, zh);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(new lucene.CustomSimilarity());
        config.setWriteLockTimeout(3 * 1000);

        File indexFile = new File(PATH);
        FSDirectory indexDir = FSDirectory.open(indexFile, new SimpleFSLockFactory());
        return new IndexWriter(indexDir, config);

    }
}
