package main.java.algorithm.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 三叉前缀树
 */
public class TernarySearchTrie {
    public static void main(String[] args) {

        TernarySearchTrie tree = new TernarySearchTrie();

        tree.addWord("xa");
        tree.addWord("xb");
        tree.addWord("xac");
        tree.addWord("xd");
        System.out.println("search:");
        Entry node = tree.get("x");
        System.out.println(node);
        List<String> rr=tree.searchSimilar("x",10);
        System.out.println(rr);
    }

    private Entry root = new Entry();

    Entry addWord(String key) {
        if (key == null || key.trim().length() == 0)
            return null;
        Entry node = root;
        int i = 0;
        while (true) {
            int diff = key.charAt(i) - node.splitChar;

            if (diff == 0) {// 当前单词和上一次的相比较，如果相同
                i++;
                if (i == key.length()) {
                    node.isWord= true;
                    return node;
                }
                if (node.equals == null)
                    node.equals = new Entry(key.charAt(i));// 这里要注意，要获取新的单词填充进去，因为i++了
                node = node.equals;
            } else if (diff < 0) {// 没有找到对应的字符，并且下一个左或右节点为NULL，则会一直创建新的节点
                if (node.left == null)
                    node.left = new Entry(key.charAt(i));
                node = node.left;
            } else {
                if (node.right == null)
                    node.right = new Entry(key.charAt(i));
                node = node.right;
            }
        }
    }

    List<String> searchSimilar(String prefix, int maxMatch){
        if(prefix==null||prefix.trim().length()==0){
            return null;
        }
        List<String> entries=new ArrayList<String>();
        //返回前缀匹配节点的中间节点
        Entry node = search(prefix,entries);

        if(node!=null){
            //从中间节点开始中序遍历
            DFS(prefix,node,entries,maxMatch);
        }
        return entries;
    }
    private void DFS(String prefix,Entry node,List<String> entries, int max){
        if(node!=null&&entries.size()<=max){
            if (node.isWord) {
                entries.add(prefix+node.splitChar);
            }
            DFS(prefix,node.left, entries, max);
            //三叉trie的不同之处，节点A的左右节点不包含A的值，A的中间节点才是
            DFS(prefix+node.splitChar,node.equals, entries, max);
            DFS(prefix,node.right, entries, max);
        }
    }

    /**
     * 根据前缀查找一个节点
     * @param key 前缀
     * @param entries key是树中的一个单词 把key加入到 entries
     * @return
     * 树中不存在key开头的节点，null
     * 树中存在key开头的节点，返回key对应节点的“中间节点”
     */
    private Entry search(String key,List<String> entries) {
        if (key == null || key.trim().length() == 0)
            return null;
        Entry node = root;
        int  i = 0;
        while (true) {
            if (node == null)
                return null;
            int diff = key.charAt(i) - node.splitChar;

            if (diff == 0) {
                i++;
                if (i == key.length()) {
                    if(node.isWord){
                        entries.add(key);
                    }
                    return node.equals;
                }
                node = node.equals;
            } else if (diff < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
    }
    /**
     * key在树中的节点
     * @param key k
     * @return 不存在返回null
     */
    public Entry get(String key) {
        if (key == null || key.trim().length() == 0)
            return null;
        Entry node = root;
        int  i = 0;
        while (true) {
            if (node == null)
                return null;
            int diff = key.charAt(i) - node.splitChar;

            if (diff == 0) {
                i++;
                if (i == key.length()) {
                    if(node.isWord){
                        return node;
                    }else{
                        return null;
                    }
                }
                node = node.equals;
            } else if (diff < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
    }

    /**
     * 三叉Trie树存在3个节点，左右子节点和二叉树类似，以前key都是存放在二叉树的当前节点中，在三叉树中单词是存放在中间子树的。
     */
    static class Entry {
        Entry left;
        Entry right;
        Entry equals;// 比对成功就放到中间节点
        char splitChar;// 标记节点的单词
        boolean isWord;

        public Entry(char splitchar) {
            this.splitChar = splitchar;
        }

        public Entry() {
        }
    }
}