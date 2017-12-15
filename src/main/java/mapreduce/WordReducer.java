package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/* 
 * 继承Reducer类需要定义四个输出、输出类型泛型： 
 * 四个泛型类型分别代表： 
 * KeyIn        Reducer的输入数据的Key，这里是每行文字中的单词"hello" 
 * ValueIn      Reducer的输入数据的Value，这里是每行文字中的次数 
 * KeyOut       Reducer的输出数据的Key，这里是每行文字中的单词"hello" 
 * ValueOut     Reducer的输出数据的Value，这里是每行文字中的出现的总次数 
 */  
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
