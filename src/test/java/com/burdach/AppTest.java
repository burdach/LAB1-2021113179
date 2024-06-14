package com.burdach;

import org.graphstream.graph.Graph;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Before
    public void setUp() {
        App.processText("input.txt");
    }
    @Test
    public void testDirectPath(){//测试有直接路径的情况
        String word1 = "has";
        String word2 = "all";
        Graph graph = App.processText("input.txt");
        String result = App.calcShortestPath(graph, word1, word2);
        assertTrue(result.equals("The shortest path from 'has' to 'all' is: has->all with a length of 1"));
    }
    @Test
    public void testMultiStepPath(){//测试有多步路径的情况
        String word1 = "has";
        String word2 = "new";
        Graph graph = App.processText("input.txt");
        String result = App.calcShortestPath(graph, word1, word2);
        assertTrue(result.equals("The shortest path from 'has' to 'new' is: has->all->new with a length of 2"));
    }
    @Test
    public void testSameStartAndEndNode(){//测试起点和终点相同的情况
        String word1 = "has";
        String word2 = "has";
        Graph graph = App.processText("input.txt");
        String result = App.calcShortestPath(graph, word1, word2);
        assertTrue(result.equals("The shortest path from 'has' to 'has' is: has with a length of 0"));
    }
    @Test
    public void testTwoNodesGraph(){//测试只有两个节点的情况
        String word1 = "has";
        String word2 = "all";
        Graph graph = App.processText("twonodes.txt");
        String result = App.calcShortestPath(graph, word1, word2);
        assertTrue(result.equals("The shortest path from 'has' to 'all' is: has->all with a length of 1"));
    }
    @Test
    public void testNode2NotInGraph(){//测试节点2不在图中的情况
        String word1 = "has";
        Graph graph = App.processText("input.txt");
        String result = App.calcShortestPath(graph, word1, "notInGraph");
        assertTrue(result.equals("The word 'notInGraph' is not in the graph."));
    }
    @Test
    public void testNode1NotInGraph(){//测试节点1不在图中的情况
        String word2 = "has";
        Graph graph = App.processText("input.txt");
        String result = App.calcShortestPath(graph, "notInGraph", word2);
        assertTrue(result.equals("The word 'notInGraph' is not in the graph."));
    }
    @Test
    public void testEmptyGraph(){//测试空图的情况
        Graph graph = null;
        String result = App.calcShortestPath(graph, "has", "all");
        assertTrue(result.equals("No graph!"));
    }
}
