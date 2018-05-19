package wkq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

class Node{            //定义通信节点
	private List<String> neighborNode;    //邻居节点
	private String nodeName;              //本节点名称
	private StringBuffer search;          //路由选择缓存，用于在路由发现的过程中存储源节点到本节点的路径
	private Map<String,List<String>> route;    // 路由表   存目的地址和目的地址的完整路径，由于dsr特性，初始为空
	public StringBuffer getSearch() {     //setter和getter
		return search;
	}
	public void setSearch(StringBuffer search) {
		this.search = search;
	}
	public List<String> getNeighborNode() {
		return neighborNode;
	}
	public void setNeighborNode(List<String> neighborNode) {
		this.neighborNode = neighborNode;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public Map<String, List<String>> getRoute() {
		return route;
	}
	public void setRoute(Map<String, List<String>> route) {
		this.route = route;
	}
	
	public Node(String nodeName,List<String>neighborNode){    //节点构造函数，用于构造节点
		this.neighborNode = neighborNode;
		this.nodeName = nodeName;
		this.route = new HashMap<String,List<String>>();
		this.search = new StringBuffer("");
		init();
	}
	private void init(){               //节点构造初始化，根据邻居节点初始化路由表，存储了到邻居节点的完整路径
		Iterator<String> itor = neighborNode.iterator();
		while(itor.hasNext()){
			List<String> temp = new ArrayList<String>();
			String key = itor.next();
			String value = this.nodeName+key;
			temp.add(value);
			route.put(key, temp);
		}
	}
	
	public boolean findNode(String des){              //判断邻居节点是否为目的节点，是返回true，否返回false
		Iterator<String> itor = neighborNode.iterator();
		while(itor.hasNext()){
			String temp = itor.next();
			if(temp.equals(des)){
				search.append(temp);
				return true;
			}
		}
		return false;
	}
	

	
} 





class Tu{                     // 图 节点拓扑结构
	private Map<String,Node> total;     //存储拓扑中所有节点
	public Tu(){          //网络拓扑构造
		this.total = new HashMap<String,Node>();
	}
	 public void addNode(String nodeName,Node node){    //向网络拓扑中增加节点
		total.put(nodeName, node);
	 }
	 
	 public String printWay(String source,String destination){   //输出源节点，目的节点，返回路径选择
		 Node sour = total.get(source);
		 Map<String,List<String>> route = sour.getRoute();
			if(destination!=null&&!destination.equals("")){      //如果目的节点在路由表中已存在，那么直接查路由表，返回路径即可
				if(route.containsKey(destination)){
					String des = route.get(destination).get(0);
					return des;
				}
				else{                                            //如果目的节点在路由表中还没有发现到达路径，开启路由发现
					findWay(source,destination);                //路由发现开始
					String des = route.get(destination).get(0);
					return des;
				}
			}
			else{
				return "error";           //如果目的节点不再网络拓扑中，返回error
			}
		}
		
		public void findWay(String source,String destination){    //路由表中没有直接到到目的节点的路径，开始路由发现
			Node sour = total.get(source);
			Queue<Node> queue = new LinkedBlockingQueue<Node>();   //采取广度优先搜索，找到达目的节点最近的路径，因此需要借助一个队列
			List<String> listTemp = sour.getNeighborNode();
			Iterator<String> itor = listTemp.iterator();
			while(itor.hasNext()){                                //由于目的节点没在路由表中发现，因此需要借助邻居节点继续寻找，所以要先把邻居节点的路由缓存更新
				String stemp = itor.next();
				Node temp = total.get(stemp);
				temp.setSearch(new StringBuffer(source+temp.getNodeName()));
				queue.add(temp);
			}
			findWayImp(sour,queue,destination);
		}
		
		public void findWayImp(Node sour,Queue<Node>queue,String destination){  //路由发现功能具体实现
			while(!queue.isEmpty()){                  //广度优先搜索队列
				Node nodeTemp = queue.poll();
				boolean flag = nodeTemp.findNode(destination);   //flag相当于路由应答，如果为true，则说明找到了目的节点，false则说明还没找到，需要继续借助邻居节点查找
				if(flag){
					List<String> li = new ArrayList<String>();
					li.add(nodeTemp.getSearch().toString());
					sour.getRoute().put(destination,li);
					return;                               //找到了目的节点直接返回
				}
				//没找到目的节点，继续下面操作，设置邻居节点缓存，并将其加入队列
				List<String> listTemp2 = nodeTemp.getNeighborNode();   
				Iterator<String> itor2 = listTemp2.iterator();
				while(itor2.hasNext()){                            //这个循环同上，是设置邻居节点的路由缓存。
					String stemp2 = itor2.next();
					Node temp2 = total.get(stemp2);
					temp2.setSearch(new StringBuffer(nodeTemp.getSearch().toString()+temp2.getNodeName()));
					queue.add(temp2);                    //将邻居节点放入队列中，继续通过循环查找 
				}
				
			}
		}
	
}

/*
测试网络拓扑模型：
           g
           |
           |
           |
     b ----d-----e------f        
     |     |
     |     |
     |     |
     a-----c
*/

public class Dsr {     //测试
	public static void main(String[] args) {
		//建立abcdefg 7个节点，并给出邻居节点，
		List<String> node1neigh = new ArrayList<String>();     
		node1neigh.add("b");                        //设置邻居节点
		node1neigh.add("c");
		Node node1 = new Node("a",node1neigh);      //创建a节点
		
		
		List<String> node2neigh = new ArrayList<String>();
		node2neigh.add("d");
		node2neigh.add("a");
		Node node2 = new Node("b",node2neigh);
		
		
		List<String> node3neigh = new ArrayList<String>();
		node3neigh.add("d");
		node3neigh.add("a");
		Node node3 = new Node("c",node3neigh);
		
		List<String> node4neigh = new ArrayList<String>();
		node4neigh.add("e");
		node4neigh.add("g");
		node4neigh.add("b");
		node4neigh.add("c");
		Node node4 = new Node("d",node4neigh);
		
		
		List<String> node5neigh = new ArrayList<String>();
		node5neigh.add("f");
		node5neigh.add("d");
		Node node5 = new Node("e",node5neigh);
		
		List<String> node6neigh = new ArrayList<String>();
		node6neigh.add("e");
		Node node6 = new Node("f",node6neigh);
		
		List<String> node7neigh = new ArrayList<String>();
		node7neigh.add("d");
		Node node7 = new Node("g",node7neigh);
		
		
		Tu t = new Tu();           //初始化网络拓扑
		t.addNode("a", node1);
		t.addNode("b", node2);
		t.addNode("c", node3);
		t.addNode("d", node4);
		t.addNode("e", node5);
		t.addNode("f", node6);
		t.addNode("g", node7);
		
		String result = t.printWay("a", "f");  //给出源节点a  ，请求目的节点f ，返回路由选择
		System.out.println(result);
		
	}
}
