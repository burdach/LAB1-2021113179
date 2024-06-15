package com.burdach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkGraphML;


/**
 * 软工实验一
 *
 */
public class App 
{
    static Graph graph; // Package-private
    private static final Random RANDOM = new Random();
    public static void main(String[] args) {
        String filePath = "input.txt"; // 文件路径   
        graph = processText(filePath);
        showDirectedGraph(graph);
    
        // 使用 try-with-resources 来创建并管理 Scanner 对象
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            // 询问用户是否要使用查询桥接词功能
            System.out.println("是否要使用查询桥接词功能？(输入 y 使用，输入 n 跳过)");
            String input = scanner.nextLine().toLowerCase(); // 将输入转换为小写
            switch (input) {
                case "y" -> {
                    // 调用查询桥接词函数
                    query(scanner);
                }
                case "n" -> System.out.println("已跳过查询桥接词功能。");
                default -> System.out.println("无效的输入。");
            }
    
            // 询问用户是否要使用根据bridge word生成新文本
            System.out.println("是否要使用根据bridge word生成新文本功能?(输入 y 使用，输入 n 跳过)");
            String input2 = scanner.nextLine().toLowerCase(); // 将输入转换为小写
            switch (input2) {
                case "y" -> {
                    // 调用根据BridgeWords生成新文本功能
                    generate(scanner);
                }
                case "n" -> System.out.println("已跳过根据BridgeWords生成新文本功能。");
                default -> System.out.println("无效的输入。");
            }
    
            // 询问用户是否要使用查询最短路径功能
            System.out.println("是否要使用查询最短路径功能?(输入 y 使用，输入 n 跳过)");
            String input3 = scanner.nextLine().toLowerCase(); // 将输入转换为小写
            switch (input3) {
                case "y" -> {
                    // 调用查询最短路径功能
                    shortestPath(scanner);
                }
                case "n" -> System.out.println("已跳过查询最短路径功能。");
                default -> System.out.println("无效的输入。");
            }
    
            // 询问用户是否要使用随机游走功能
            System.out.println("是否要使用随机游走功能?(输入 y 使用，输入 n 跳过)");
            String input4 = scanner.nextLine().toLowerCase(); // 将输入转换为小写
            switch (input4) {
                case "y" -> {
                    String walknodes = randomWalk(scanner);
                    System.out.println("本次随机游走经过的结点为：\n");
                    System.out.println(walknodes);
                }
                case "n" -> System.out.println("已跳过随机游走功能。");
                default -> System.out.println("无效的输入。");
            }
        } // try-with-resources 会自动关闭 scanner
    }
    
    public static void query(Scanner scanner) {     //桥接词查询
        System.out.println("请输入word1: ");
        String word1 = scanner.nextLine();
        System.out.println("请输入word2: ");
        String word2 = scanner.nextLine();
        // 调用TextToGraph类中的QueryBridgeWords函数
        String result = queryBridgeWords(graph, word1, word2);
        System.out.println(result);
    }

    public static void generate(Scanner scanner)          //随机文本生成
    {
        // 用户输入的新文本
        System.out.println("请输入一行新文本：");
        String inputText = scanner.nextLine();
        String newText = generateNewText(inputText);
        // 输出新文本
        System.out.println("生成的新文本：");
        System.out.println(newText);
    }
    public static void shortestPath(Scanner scanner) {        //最短路径查询
        System.out.println("请输入word1: ");
        String word1 = scanner.nextLine();
        System.out.println("请输入word2: ");
        String word2 = scanner.nextLine();
        String result = calcShortestPath(graph, word1, word2);
        System.out.println(result);
    }
    public static Graph processText(String filePath) { // 生成图
        Graph localGraph = new SingleGraph("TextGraph");
    
        try {
            // 创建文件对象
            File file = new File(filePath);
    
            // 使用 try-with-resources 来创建并管理 Scanner 对象
            try (Scanner scanner = new Scanner(file, "UTF-8")) {
                // 使用 Map 记录单词和其出现次数
                Map<String, Integer> wordCount = new HashMap<>();
                // 创建一个 StringBuilder 对象用于保存处理后的文本内容
                StringBuilder processedText = new StringBuilder();
                // 逐行读取文本内容并处理
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
    
                    // 只保留英文字符和空格，其他字符替换为空格
                    line = line.replaceAll("[^a-zA-Z\\s]+", " ");
    
                    // 将多个连续空格替换为单个空格
                    line = line.replaceAll("\\s+", " ");
    
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
                    if (localGraph.getNode(word) == null) {
                        localGraph.addNode(word);
                    }
    
                    // 添加边到图中
                    if (i > 0) {
                        String previousWord = words[i - 1].toLowerCase(); // 不区分大小写
                        String edgeId = previousWord + "-" + word;
    
                        // 在添加边之前检查是否已经存在
                        if (localGraph.getEdge(edgeId) == null) {
                            localGraph.addEdge(edgeId, previousWord, word, true);
                        }
                    }
                }
    
                // 设置边的权重为单词相邻出现的次数
                localGraph.edges().forEach(edge -> {
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
                });
    
                // 保存图到本地文件
                FileSinkGraphML graphML = new FileSinkGraphML();
                try {
                    graphML.writeAll(localGraph, "output.graphml");
                } catch (IOException e) {
                    System.out.println("保存图为 GraphML 文件时出错: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + e.getMessage());
        }
        return localGraph;
    }
    
    /**
     * @param graph
     */
    public static void showDirectedGraph(Graph graph ) {            //显示图
        // 打印有向图的信息
        System.out.println("有向图中的节点数量：" + graph.getNodeCount());
        System.out.println("有向图中的边数量：" + graph.getEdgeCount());
        // 可视化有向图
        System.setProperty("org.graphstream.ui", "swing");
        // 设置样式表
        String styleSheet = 
            "graph { " +
            "   fill-color: white; " +
            "   padding: 60px; " +
            "} " +
            "node { " +
            "   fill-color: lightblue; " +
            "   size: 90px, 35px; " +
            "   shape: box; " + // 修改为矩形
            "   stroke-mode: plain; " +
            "   stroke-color: black; " +
            "   stroke-width: 1px; " +
            "   text-mode: normal; " +
            "   text-size: 18px; " +
            "   z-index: 0; " +
            "} " +
            "node#space { " +
            "   size: 200px, 200px; " +
            "} " +
            "edge { " +
            "   fill-color: grey; " +
            "   arrow-size: 20px, 12px; " +
            "   size: 1px; " + // 增大边的宽度
            "   text-alignment: under; " +
            "   text-size: 30px; " +
            "} " +
            "edge .highlighted { " +
            "   fill-color: red; " +
            "   size: 6px; " +
            "   arrow-size: 24px, 16px; " +
            "} ";

        graph.setAttribute("ui.stylesheet", styleSheet);

        // 设置节点标签
        graph.nodes().forEach(node -> {
            node.setAttribute("ui.label", node.getId());
        });
        
        // 设置边标签和初始样式
        graph.edges().forEach(edge -> {
            Object weightObj = edge.getAttribute("weight");
            if (weightObj instanceof Integer) {
                int weight = Integer.parseInt(weightObj.toString());
                edge.setAttribute("ui.label", Integer.toString(weight));
            }
            edge.setAttribute("ui.class", "default");
            });

        // 显示图形
        graph.display();

    }
    public static String queryBridgeWords(Graph graph, String word1, String word2)
    {
        if (graph == null) {
            return "No graph!";
        }
        //检查word1和word2是否在图中存在
        if(graph.getNode(word1)==null || graph.getNode(word2)==null)
        {
            // System.out.println("No word1 or word2 in the graph!");
            return "No word1 or word2 in the graph!";
        }
        List<String> bridgeNodes = findMiddleNodes(graph, word1, word2);
        // 定义一个字符串
        String result = printMiddleNodes(bridgeNodes);
        // printMiddleNodes(bridgeNodes);
        return result;
    }
    public static List<String> getOutgoingNodes(Graph graph, String word1) {
        List<String> outgoingNodes = new ArrayList<>();
        
        // 获取节点 word1
        // Node node1 = App.graph.getNode(word1);
        Node node1 = graph.getNode(word1);
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
    public static List<String> findMiddleNodes(Graph graph, String word1, String word2) {
        List<String> middleNodes = new ArrayList<>();
    
        // 获取与 word1 相连的节点
        List<String> outgoingWord1 = getOutgoingNodes(graph, word1);
    
        // 遍历与 word1 相连的节点
        for (String node : outgoingWord1) {
            // 获取与相连节点相连的节点
            List<String> outgoingNode = getOutgoingNodes(graph, node);
            // 如果相连节点中包含 word2，则将中间节点添加到列表中
            if (outgoingNode.contains(word2)) {
                middleNodes.add(node);
            }
        }
    
        return middleNodes;
    }
    public static String printMiddleNodes(List<String> middleNodes) {
        StringBuilder result = new StringBuilder("The bridge words from word1 to word2 are:");
        if(middleNodes.isEmpty())
        {
            //桥接词列表为空
            // System.out.println("No bridge words from word1 to word2!");
            return "No bridge words from word1 to word2!";
        }
        for (int i = 0; i < middleNodes.size(); i++) {
            result.append(middleNodes.get(i));
            if (i < middleNodes.size() - 1) {
                result.append(", ");
            }
        }
        // System.out.println(result.toString());
        return result.toString();
    }
    public static String generateNewText(String inputText)
    {
        String[] words = inputText.split("\\s+");
        StringBuilder newText = new StringBuilder();
        // Random random = new Random();
        for (int i=0;i<words.length-1;i++)
        {
            newText.append(words[i]).append(" ");
            List<String> bridgeWords = findMiddleNodes(graph, words[i], words[i+1]);
            if (!bridgeWords.isEmpty())
            {
                String bridgeWord = bridgeWords.get(RANDOM.nextInt(bridgeWords.size()));
                newText.append(bridgeWord).append(" ");
            } 
        }
        newText.append(words[words.length-1]);
        return newText.toString();
    }
    public static String calcShortestPath(Graph graph, String word1, String word2) {
        if (graph == null) {
            return "No graph!";
        }
        // 检查输入的单词是否在图中
        if (graph.getNode(word1) == null) {
            return "The word '" + word1 + "' is not in the graph.";
        }
        if (graph.getNode(word2) == null) {
            return "The word '" + word2 + "' is not in the graph.";
        }
        // 构建邻接矩阵
        int[][] adjacencyMatrix = buildAdjacencyMatrix(graph);
        // 使用数组来保存起始点到各点的最短距离
        int[] dist = new int[graph.getNodeCount()];
        // 使用数组来记录各点是否已经确定最短路径
        boolean[] visited = new boolean[graph.getNodeCount()];
        // 使用数组来记录路径
        int[] path = new int[graph.getNodeCount()];
        // 初始化距离数组
        for (int i = 0; i < graph.getNodeCount(); i++) {
            dist[i] = Integer.MAX_VALUE;
            visited[i] = false;
            path[i] = -1;
        }
        // 起始点到自身的距离为0
        dist[graph.getNode(word1).getIndex()] = 0;
        // 找到起始点到目标点的最短路径
        for (int count = 0; count < graph.getNodeCount() - 1; count++) {
            int u = minDistance(dist, visited);
            visited[u] = true;

            for (int v = 0; v < graph.getNodeCount(); v++) {
                if (!visited[v] && adjacencyMatrix[u][v] != 0 && dist[u] + adjacencyMatrix[u][v] < dist[v]) {
                    dist[v] = dist[u] + adjacencyMatrix[u][v];
                    path[v] = u;
                }
            }}
         // 构建路径
        List<Integer> shortestPath = new ArrayList<>();
        int targetIndex = graph.getNode(word2).getIndex();
        int current = targetIndex;
        while (current != -1) {
            shortestPath.add(current);
            current = path[current];
        }
        Collections.reverse(shortestPath);
        // 构建路径字符串
        StringBuilder pathString = new StringBuilder();
        for (int i = 0; i < shortestPath.size(); i++) {
            if (i != 0) {
                pathString.append("->");
            }
            pathString.append(graph.getNode(shortestPath.get(i)).getId());
        }
        // highlightPath(shortestPath);
        // 返回结果
        return "The shortest path from '" + word1 + "' to '" + word2 + "' is: " + pathString.toString()
                + " with a length of " + dist[targetIndex];
    }

    public static void highlightPath(List<Integer> shortestPath) {
        // 清除之前的高亮显示
        graph.edges().forEach(edge -> edge.setAttribute("ui.class", "default"));

        // 突出显示当前路径
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            String sourceId = graph.getNode(shortestPath.get(i)).getId();
            String targetId = graph.getNode(shortestPath.get(i + 1)).getId();
            Edge edge = graph.getEdge(sourceId + "-" + targetId);
            if (edge != null) {
                edge.setAttribute("ui.class", "highlighted");
            }
        }
    }
    
    
    private static int[][] buildAdjacencyMatrix(Graph graph) {
        int[][] adjacencyMatrix = new int[graph.getNodeCount()][graph.getNodeCount()];
    
        graph.edges().forEach(edge -> {
            String[] nodes = edge.getId().split("-");
            int sourceIndex = graph.getNode(nodes[0]).getIndex();
            int targetIndex = graph.getNode(nodes[1]).getIndex();
            int weight = (int) edge.getAttribute("weight");
            adjacencyMatrix[sourceIndex][targetIndex] = weight;
        });
    
        return adjacencyMatrix;
    }
    private static int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
    
        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] < min) {
                min = dist[v];
                minIndex = v;
            }
        }
    
        return minIndex;
    }
    public static String randomWalk(Scanner scanner)
    {
        //随机选择一个初始节点
        String startNode = getRandomStartNode();
        if (startNode == null)
        {
            return "这是一个空图，不能进行随机游走！";
        }
        //存储遍历过的节点和边
        List<String> visitedNodes = new ArrayList<>();
        List<String> visitedEdges = new ArrayList<>();
        //开始遍历
        System.out.println("Starting random walk from node: " + startNode);
        String currentNode = startNode;
        while (true)
        {
            visitedNodes.add(currentNode);
            //获取当前节点的出边
            List<String> outgoingNodes = getOutgoingNodes(graph, currentNode);
            if (outgoingNodes.isEmpty())
            {
                // 当前节点不存在出边，遍历结束
                System.out.println("No outgoing edges from node: " + currentNode);
                break;
            }
            // 显示当前节点和下一个可能的节点
            System.out.println("当前节点: " + currentNode);
            System.out.println("可能的下一个节点: " + outgoingNodes);
            System.out.println("是否继续游走？(输入 y 继续，输入 n 停止)");
            String input = scanner.nextLine().toLowerCase();
            if (!input.equals("y")) {
                break;
            }
            //随机选择一个出边
            String nextNode = outgoingNodes.get((int) (Math.random() * outgoingNodes.size()));
            String edge = currentNode + "-" + nextNode;
            if (visitedEdges.contains(edge))
            {
                // 出现重复边，遍历结束
                System.out.println("Loop detected. Stopping random walk.");
                break;
            }
            visitedEdges.add(edge);
            currentNode = nextNode;
        }
        // 输出遍历结果到文本文件
        writeWalkToFile(visitedNodes, visitedEdges);
        return String.join(" --> ", visitedNodes);
    }
    private static String getRandomStartNode() {
        // 从图中随机选择一个节点作为起始节点
        List<String> nodes = new ArrayList<>(graph.nodes().map(Node::getId).collect(Collectors.toList()));
        if (nodes.isEmpty()) {
            return null;
        }
        return nodes.get((int) (Math.random() * nodes.size()));
    }
    private static void writeWalkToFile(List<String> visitedNodes, List<String> visitedEdges) {
        try (FileWriter writer = new FileWriter("random_walk_output.txt", StandardCharsets.UTF_8)) {
            writer.write("Nodes visited:\n");
            for (String node : visitedNodes) {
                writer.write(node + "\n");
            }
            writer.write("\nEdges visited:\n");
            for (String edge : visitedEdges) {
                writer.write(edge + "\n");
            }
            System.out.println("\nRandom walk results written to random_walk_output.txt\n");
        } catch (IOException e) {
            System.out.println("\nError writing random walk results to file: \n" + e.getMessage());
        }
    }
}

