package com.github.caryyu.lucene.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by cary on 6/15/17.
 */
public class LuceneExampleApplication {
    public static void main(String[] args) {
        Directory directory = new RAMDirectory();
        try {
            writeIndex(directory);
            readIndex(directory);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                directory.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeIndex(Directory directory) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        List<Document> list = mockDocuments();
        list.stream().forEach(document -> {
            try {
                indexWriter.addDocument(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        indexWriter.close();
    }

    private static void readIndex(Directory directory) throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        Query query = new TermQuery(new Term("content","海"));
        TopDocs topDocs = indexSearcher.search(query,100);

        Stream.of(topDocs.scoreDocs).forEach(item -> {
            try {
                Document d = indexSearcher.doc(item.doc);
                System.out.println(d.getField("content").stringValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        directoryReader.close();
    }

    private static List<Document> mockDocuments() {
        List<Document> list = new ArrayList<Document>();
        Document document1 = new Document();
        document1.add(new Field("content","我爱上海的美景", TextField.TYPE_STORED));

        Document document2 = new Document();
        document2.add(new Field("content","我爱中国的上海", TextField.TYPE_STORED));

        Document document3 = new Document();
        document3.add(new Field("content","我爱全世界的上海 i love Shanghai", TextField.TYPE_STORED));

        list.add(document1);
        list.add(document2);
        list.add(document3);
        return list;
    }
}
