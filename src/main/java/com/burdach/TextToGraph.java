package com.burdach;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.util.ArrayList;
import java.util.List;
public class TextToGraph {
    public static Graph graph;
    //将graph设置为全局静态变量
    public static void main(String[] args) {
        String filePath = "input.txt"; // 文件路径

        // 检查是否提供了文件路径参数
        if (args.length > 0) {
            filePath = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入文本文件的路径和文件名：");
            filePath = scanner.nextLine();
            scanner.close();
        }
        // 调用处理文本的方法
        graph = processText(filePath);
        showDirectedGraph(graph);
    }

    public static Graph processText(String filePath) {
        Graph graph = new SingleGraph("TextGraph");

        try {
            // 创建文件扫描器
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            // 使用 Map 记录单词和其出现次数
            Map<String, Integer> wordCount = new HashMap<>();
            // 创建一个 StringBuilder 对象用于保存处理后的文本内容
            StringBuilder processedText = new StringBuilder();
            // 逐行读取文本内容并处理
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // 只保留英文字符和空格，其他字符替换为空格
                line = line.replaceAll("[^a-zA-Z\\s]+", " ");
                processedText.append(line).append(" ");
            }
            // 将处理后的文本内容转换为一行
            String processedLine = processedText.toString().trim();
            // 输出整理好的字符串到控制台
            System.out.println("整理后的文本内容：");
            System.out.println(processedLine);
            // 分割单词
            String[] words = processedLine.split("\\s+");

            // 更新单词出现次数
            for (int i = 0; i < words.length; i++) {
                String word = words[i].toLowerCase(); // 不区分大小写
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);

                // 添加节点到图中
                if (graph.getNode(word)==null) {
                    graph.addNode(word);
                }

                // 添加边到图中
                if (i > 0) {
                    String previousWord = words[i - 1].toLowerCase(); // 不区分大小写
                    graph.addEdge(previousWord + "-" + word, previousWord, word, true);
                }
            }
            // 关闭文件扫描器
            scanner.close();
            // 设置边的权重为单词相邻出现的次数
        graph.edges().forEach(edge -> {
            String[] nodes = edge.getId().split("-");
            String source = nodes[0];
            String target = nodes[1];
            int weight = 0;

            // 计算边的权重为文本中边的两个节点相邻出现的次数
            for (int i = 0; i < words.length - 1; i++) {
                String currentWord = words[i].toLowerCase();
                String nextWord = words[i + 1].toLowerCase();

                // 如果当前单词和下一个单词分别是边的源节点和目标节点，则增加权重
                if (currentWord.equals(source) && nextWord.equals(target)) {
                    weight++;
                }
            }

        // 将计算得到的权重设置为边的属性
        edge.setAttribute("weight", weight);
        });}
        catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + e.getMessage());
        }

        return graph;
    }
    public static void showDirectedGraph(Graph graph ) {
        // 打印有向图的信息
        System.out.println("有向图中的节点数量：" + graph.getNodeCount());
        System.out.println("有向图中的边数量：" + graph.getEdgeCount());
        // 可视化有向图
        System.setProperty("org.graphstream.ui", "swing");
        // 设置节点标签
        graph.nodes().forEach(node -> {
            node.setAttribute("ui.label", node.getId());
            node.setAttribute("ui.style", "text-size: 48;"); // 设置节点标签字体大小为 48
        });
        // 设置边标签
        graph.edges().forEach(edge -> {
            Object weightObj = edge.getAttribute("weight");
            if (weightObj instanceof Integer) {
                int weight = (int) weightObj;
                edge.setAttribute("ui.label", Integer.toString(weight));
            } else {
                // 处理类型不匹配的情况，例如设定一个默认值
                edge.setAttribute("ui.label", "Unknown");
            }
            edge.setAttribute("ui.style", "text-size: 36;"); // 设置边标签字体大小为 36
            edge.setAttribute("ui.size", 8); //设置边的粗细为8
        });

        // 创建图形界面
        graph.display();
        // 保存图形为PNG格式
    }
    public static void queryBridgeWords(String word1, String word2)
    {
        
        //检查word1和word2是否在图中存在
        if(graph.getNode(word1)==null || graph.getNode(word2)==null)
        {
            System.out.println("No word1 or word2 in the graph!");
        }
        List<String> bridgeNodes = findMiddleNodes(word1, word2);
        printMiddleNodes(bridgeNodes);
    }
    public static String generateNewText(String inputText)
    {
        String[] words = inputText.split("\\s+");
        StringBuilder newText = new StringBuilder();
        Random random = new Random();

        for (int i=0;i<words.length-1;i++)
        {
            newText.append(words[i]).append(" ");
            List<String> bridgeWords = findMiddleNodes(words[i], words[i+1]);
            if (!bridgeWords.isEmpty())
            {
                String bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
                newText.append(bridgeWord).append(" ");
            } 
        }
        newText.append(words[words.length-1]);
        return newText.toString();
    }
    public static String calcShortestPath(String word1, String word2)
    {
        String results = null;
        
        return results;
    }
    public static String randonWalk()
    {
        String results=null;
        return results;
    }
    public static List<String> getOutgoingNodes(String word1) {
        List<String> outgoingNodes = new ArrayList<>();
        
        // 获取节点 word1
        Node node1 = App.graph.getNode(word1);
        if (node1 == null) {
            // 如果节点 word1 不存在，则返回空列表
            return outgoingNodes;
        }
        // 获取节点 word1 的出边集合
        int outEdgeCount = node1.getOutDegree();
        // 遍历出边集合，获取每条边的目标节点
        for (int i = 0; i < outEdgeCount; i++) {
            Edge edge = node1.getLeavingEdge(i);
            Node targetNode = edge.getTargetNode();
            if (targetNode != null) {
                outgoingNodes.add(targetNode.getId());
            }
    } 
        return outgoingNodes;
    }
    public static List<String> findMiddleNodes(String word1, String word2) {
        List<String> middleNodes = new ArrayList<>();
    
        // 获取与 word1 相连的节点
        List<String> outgoingWord1 = getOutgoingNodes(word1);
    
        // 遍历与 word1 相连的节点
        for (String node : outgoingWord1) {
            // 获取与相连节点相连的节点
            List<String> outgoingNode = getOutgoingNodes(node);
            // 如果相连节点中包含 word2，则将中间节点添加到列表中
            if (outgoingNode.contains(word2)) {
                middleNodes.add(node);
            }
        }
    
        return middleNodes;
    }
    public static void printMiddleNodes(List<String> middleNodes) {
        StringBuilder result = new StringBuilder("The bridge words from word1 to word2 are:");
        if(middleNodes.size()==0)
        {
            //桥接词列表为空
            System.out.println("No bridge words from word1 to word2!");
            return;
        }
        for (int i = 0; i < middleNodes.size(); i++) {
            result.append(middleNodes.get(i));
            if (i < middleNodes.size() - 1) {
                result.append(", ");
            }
        }
        System.out.println(result.toString());
    }
    
}
