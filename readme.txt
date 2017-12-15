java开发MapReduce程序
一、maven项目加入相关依赖包
hadoop-common 2.7.3
hadoop-hdfs 2.7.3
hadoop-mapreduce-client-core 2.7.3
jdk.tools ${JAVA_HOME}/lib/tools.jar

二、Mapper代码开发
public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	//重写Map方法
	@Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
		// 获得每行文档内容，并且进行拆分 
		String line = value.toString();
		String[] words = line.split(" ");
		// 遍历拆分的内容 
		for(String word : words) {
			// 每出现一次则在原来的基础上：+1  
			context.write(new Text(word), new IntWritable(1));
		}
	}
}

三、Reducer代码开发
public class WordReducer extends Reducer<Text,IntWritable,Text,LongWritable> {

	//重写reduce方法 
	@Override
    protected void reduce(Text key, Iterable<IntWritable> values,
            Reducer<Text, IntWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
		long count = 0;
		for(IntWritable v : values) {
			// i.get转换成long类型 
			count += v.get();
		}
		// 输出总计结果 
		context.write(key, new LongWritable(count));
	}
}

四、Hadoop Job WordCount代码开发
public class HadoopWCJob  {

	public static void main(String[] args) throws Exception{
		// 创建job对象  
		Job job = Job.getInstance(new Configuration());
		// 指定程序的入口 
		job.setJarByClass(HadoopWCJob.class);
		
		// 指定自定义的Mapper阶段的任务处理类  
		job.setMapperClass(WordMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		// 数据HDFS文件服务器读取数据路径
		FileInputFormat.setInputPaths(job, "/hadoop/words.txt");
		
		// 指定自定义的Reducer阶段的任务处理类  
		job.setReducerClass(WordReducer.class);
		// 设置最后输出结果的Key和Value的类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		// 将计算的结果上传到HDFS服务 
		FileOutputFormat.setOutputPath(job, new Path("/hadoop/wordsResult"));
		
		// 执行提交job方法，直到完成，参数true打印进度和详情
		job.waitForCompletion(true);
		System.out.println("Finished");  
	}
}

五、项目打成jar包，制定Main Class类，上传至服务器上
选择MapReduceWC项目->右击菜单->Export…，在弹出的提示框中选择Java下的JAR file：
设置导出jar名称和路径，选择Next>:
设置程序的入口，设置完成后，点击Finish：
生成wc.jar如下文件

六、服务器上创建words.txt至/usr/local/hadoop/hadoop_temp/words.txt
hello tom
hello jerry
hello kitty
hello world
hello tom
hello java
hello jerry
hello kitty
hello world
hello c

七、上传该测试文件到hadoop的/hadoop/目录上
bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/words.txt /hadoop/

八、通过bin/hadoop jar /usr/local/hadoop/hadoop_temp/wc.jar来运行示例程序
Running job: job_1513155455003_0004
17/12/15 11:29:02 INFO mapreduce.Job: Job job_1513155455003_0004 running in uber mode : false
17/12/15 11:29:02 INFO mapreduce.Job:  map 0% reduce 0%
17/12/15 11:29:06 INFO mapreduce.Job:  map 100% reduce 0%
17/12/15 11:29:10 INFO mapreduce.Job:  map 100% reduce 100%
17/12/15 11:29:10 INFO mapreduce.Job: Job job_1513155455003_0004 completed successfully
17/12/15 11:29:10 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=237
		FILE: Number of bytes written=238001
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=216
		HDFS: Number of bytes written=50
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=1440
		Total time spent by all reduces in occupied slots (ms)=1486
		Total time spent by all map tasks (ms)=1440
		Total time spent by all reduce tasks (ms)=1486
		Total vcore-milliseconds taken by all map tasks=1440
		Total vcore-milliseconds taken by all reduce tasks=1486
		Total megabyte-milliseconds taken by all map tasks=1474560
		Total megabyte-milliseconds taken by all reduce tasks=1521664
	Map-Reduce Framework
		Map input records=10
		Map output records=20
		Map output bytes=191
		Map output materialized bytes=237
		Input split bytes=106
		Combine input records=0
		Combine output records=0
		Reduce input groups=7
		Reduce shuffle bytes=237
		Reduce input records=20
		Reduce output records=7
		Spilled Records=40
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=76
		CPU time spent (ms)=940
		Physical memory (bytes) snapshot=434761728
		Virtual memory (bytes) snapshot=4239069184
		Total committed heap usage (bytes)=320864256
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=110
	File Output Format Counters 
		Bytes Written=50
Finished

九、查看执行结果
bin/hadoop fs -ls /hadoop/wordsResult

十、查看WordCount结果
bin/hadoop fs -cat /hadoop/wordsResult/part-r-00000
c	1
hello	10
java	1
jerry	2
kitty	2
tom	2
world	2