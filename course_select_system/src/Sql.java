/**
 * 项目名称：	选课管理系统
 * 作者：		贺自怡
 * 日期：		2016/12/12
 * 环境：		MacOS Sierra
 * 			MySQL 5.6
 * 			JAVA 1.8
 * 			Eclipse Neon.1a
 * 
 */
package mysql_Project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
public class Sql {
	    private static final String HOST = "localhost";
	    private static final int PORT = 3306;
	    private static final String DATABASE = "course";
	    private static final String USER = "root";
	    private static final String PASSWORD = null;
	    
	    private static final String COURSE_INSERT_TEMPLATE = "INSERT INTO course values(%d,'%s','%s','%s','%s',%d)";
	    private static final String COURSESELECT_INSERT_TEMPLATE = "INSERT INTO course_select values('%s',%d)";
	    
	    private static final int min_credit = 6;
	    
	    public static void main(String[] args) throws Exception {
	    // preparation for JDBC, this is the only difference between MySQL and SQL Server
		/* For SQL Server, your code will be like: 
	         * Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         * Connection connection = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=bank", "root", "123"); 
	         * */
//------连接服务器--------
		Class.forName("com.mysql.jdbc.Driver");
	        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
	        println("JDBC URL: " + url);
	        Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
	        println("Connection Success!");
//-------准备数据-------
	        /** initial snapshot of database:
	         * Table dept:
	         * +----------+------------+
	         * |dept_name |   campus   |
	         * +----------+------------+
	         * |     CS   |    YQ      |
	         * +----------+------------+
	         * |     CHE  |    ZJG     |      
	         * +----------+------------+
	         * |     MED  |    HJC     |      
	         * +----------+------------+
	         * Table student:
	         * +----------+------------+-----------+
	         * |    name  | student_id | dept_name |
	         * +----------+------------+-----------+
	         * |    Tom   |    3140    |   CS      |
	         * +----------+------------+-----------+
	         * |    Mary  |    3141    |   CS      |     
	         * +----------+------------+-----------+
	         * |    Tim   |    3142    |   CS      |     
	         * +----------+------------+-----------+
	         * |    Leo   |    3143    |   CHE     |     
	         * +----------+------------+-----------+
	         * |    Mike  |    3144    |   CHE     |     
	         * +----------+------------+-----------+
	         * |   Hazel  |    3145    |   MED     |     
	         * +----------+------------+-----------+
	         * |    John  |    3146    |   MED     |     
	         * +----------+------------+-----------+
	         * Table teacher:
	         * +----------+------------+-----------+
	         * |    name  |teacher_id  |dept_name  |
	         * +----------+------------+-----------+
	         * |   Tony   |    1001    |   CS      |
	         * +----------+------------+-----------+
	         * |   Jay    |    1002    |   CS      |
	         * +----------+------------+-----------+
	         * |   Coco   |    1003    |   CHE     |
	         * +----------+------------+-----------+
	         * |   Jim    |    1004    |   MED     |
	         * +----------+------------+-----------+
	         * Table course:
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |course_id |course_name  |  time     |   place   |teacher_id | credit |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     1    |DataStructure|  Monday   |Classroom1 |  1001     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     2    |     OOP     |  Tuesday  |Classroom2 |  1001     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     3    | OperatingS  |  Friday   |Classroom1 |  1001     |  4     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     4    |  Database   |  Monday   |Classroom2 |  1002     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     5    |  Graphics   |  Wednesday|Classroom1 |  1002     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     6    | InorganicChe|  Tuesday  |Classroom3 |  1003     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     7    |  OrganicChe |  Thursday |Classroom4 |  1003     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     8    | PhysicalChe |  Friday   |Classroom2 |  1003     |  2     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |     9    |   Anatomy   |  Monday   |Classroom2 |  1004     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |    10    |    Surgery  |  Tuesday  |Classroom1 |  1004     |  4     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * |    11    |  Psychology |  Thursday |Classroom1 |  1004     |  3     |
	         * +----------+-------------+-----------+-----------+-----------+--------+
	         * Table course_select:
	         * +----------+---------+
	         * |student_id|course_id|
	         * +----------+---------+
	         * | 3140     |  2      |
	         * +----------+---------+
	         * | 3140     |  5      |
	         * +----------+---------+
	         * | 3141     |  5      |
	         * +----------+---------+
	         * | 3141     |  3      |
	         * +----------+---------+
	         * | 3141     |  4      |
	         * +----------+---------+
	         * | 3142     |  1      |
	         * +----------+---------+
	         * | 3142     |  7      |
	         * +----------+---------+
	         * | 3143     |  6      |
	         * +----------+---------+
	         * | 3143     |  7      |
	         * +----------+---------+
	         * | 3144     |  6      |
	         * +----------+---------+
	         * | 3144     |  7      |
	         * +----------+---------+
	         * | 3144     |  8      |
	         * +----------+---------+
	         * | 3145     |  9      |
	         * +----------+---------+
	         * | 3145     |  10     |
	         * +----------+---------+
	         * | 3146     |  11     |
	         * +----------+---------+
	         * 
	         */
//-----数据准备---------
//	        {
//	            Statement stat = connection.createStatement();
//	            String sql;
//	            // 这是一个很小很小的大学……只有很少的学生和老师和系……
//	            String DEPT_INSERT_TEMPLATE = "INSERT INTO dept values('%s','%s')";
//	    	    String STUDENT_INSERT_TEMPLATE = "INSERT INTO student values('%s','%s','%s')";
//	    	    String TEACHER_INSERT_TEMPLATE = "INSERT INTO teacher values('%s','%s','%s')";
//	            // 学院信息
//	            sql = String.format(DEPT_INSERT_TEMPLATE, "CS","YQ");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(DEPT_INSERT_TEMPLATE, "CHE","ZJG");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(DEPT_INSERT_TEMPLATE, "MED","HJC");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            // 学生信息
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Tom", "3140", "CS");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Mary","3141", "CS");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Tim","3142", "CS");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Leo","3143", "CHE");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Mike","3144", "CHE");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "Hazel","3145", "MED");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(STUDENT_INSERT_TEMPLATE, "John","3146", "MED");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            // 教师信息
//	            String sql = String.format(TEACHER_INSERT_TEMPLATE,  "1001","Tony", "CS");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(TEACHER_INSERT_TEMPLATE,  "1002","Jay", "CS");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(TEACHER_INSERT_TEMPLATE,  "1003", "Coco","CHE");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(TEACHER_INSERT_TEMPLATE, "1004", "Jim", "MED");
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            // 课程信息
//	            sql = String.format(COURSE_INSERT_TEMPLATE,1,"DataStructure","Monday","Classroom1","1001",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,2,"OOP","Tuesday","Classroom2","1001",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,3,"OperatingS","Friday","Classroom1","1001",4);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,4,"Database","Monday","Classroom2","1002",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,5,"Graphics","Wednesday","Classroom1","1002",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,6,"InorganicChe","Tuesday","Classroom3","1003",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,7,"OrganicChe","Thursday","Classroom4","1003",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,8,"PhysicalChe","Friday","Classroom2","1003",2);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,9,"Anatomy","Monday","Classroom2","1004",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,10,"Surgery","Tuesday","Classroom1","1004",4);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSE_INSERT_TEMPLATE,11,"Psychology","Thursday","Classroom1","1004",3);
//	            println("insert sql: " + sql);
//	            stat.executeUpdate(sql);
//	            // 选课信息
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3140", 2);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3140", 5);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3141", 5);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3141", 3);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3141", 4);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3142", 1);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3142", 7);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3143", 6);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3143", 7);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3144", 6);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3144", 7);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3144", 8);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3145", 9);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3145", 10);
//	            stat.executeUpdate(sql);
//	            sql = String.format(COURSESELECT_INSERT_TEMPLATE, "3146", 11);
//	            stat.executeUpdate(sql);
//	        }

//-------可以操作-------     
	        {
	        Statement stat = connection.createStatement();
	        String sql;
	        Scanner sc=new Scanner(System.in);
	        // 读取操作
	        String str=null;
	        println("请输入操作: (Add/Delete/Select/Update/Quit)");
	        str=sc.next();
	        ResultSet rs = stat.executeQuery("select max(course_id) from course");
			rs.next();
			int max=rs.getInt("max(course_id)");
	        while(str.compareTo("quit")!=0){
	        // 选课
	        if(str.compareTo("add")==0){
	        	int course_id,new_course;
	        	String course_time,course_dept,student_dept,student_id;
	        	println("请输入学生学号和课程编号，中间以一个空格分开：");
	        	student_id=sc.next();
	        	course_id=sc.nextInt();
	        	while ((Integer.parseInt(student_id)>3146)||(Integer.parseInt(student_id)<3140)||(course_id<1)||(course_id>max)){
	        		println("输入有误，请重新输入学生学号(3140-3146)和课程编号(1-"+max+")，中间以一个空格分开：");
	        		student_id=sc.next();
		        	course_id=sc.nextInt();
	        	}
	        	// 获得学生的学院
	        	rs = stat.executeQuery("SELECT dept_name FROM course natural join course_select natural join student WHERE student_id=\""+student_id+"\"");
	        	rs.next();
	        	student_dept = rs.getString(1);
	        	// 获得该课的上课时间
	        	rs = stat.executeQuery("SELECT time FROM course WHERE course_id="+course_id);
	        	rs.next();
	        	course_time = rs.getString(1);
	        	// 判断时间是否有冲突（取某个学生某天的课
	        	rs = stat.executeQuery("SELECT course_id FROM course_select natural join course WHERE student_id =\""+student_id+"\" and time=\""+course_time+"\"");
	        	if (rs.next()){ 
	        		new_course=rs.getInt(1);
	        		if (new_course==course_id)  //如果是同一门课
	        			println("重复选课！");
	        		else
	        			println(course_time+" 课程冲突，无法选课！");}
	        	else{
	        		// 获得该课的开课学院
	        		rs = stat.executeQuery("select dept_name from course natural join teacher where course_id="+course_id);
	        		rs.next();
	        		sql = String.format(COURSESELECT_INSERT_TEMPLATE,student_id,course_id);
	        		course_dept = rs.getString(1);
	        		if (student_dept.compareTo(course_dept)!=0){
	        			println("跨学院选课("+student_dept+"->"+course_dept+")，是否确定选课?（Y/N）");
	        			if (sc.next().compareTo("Y")==0){
	        				stat.executeUpdate(sql);
	        				println("选课成功！");
	        			}
	        			else
	        				println("取消选课！");
	        		}
	        		else{
	        			stat.executeUpdate(sql);
        				println("选课成功！");
	        		}
	        	}	
	        }
	        // 退课
	        if(str.compareTo("delete")==0){
	        	String student_id;
	        	int course_id,credit_sum;
	        	println("请输入学生学号和课程编号，中间以一个空格分开：");
	        	student_id=sc.next();
	        	course_id=sc.nextInt();
	        	while ((Integer.parseInt(student_id)>3146)||(Integer.parseInt(student_id)<3140)||(course_id<1)||(course_id>max)){
	        		println("输入有误，请重新输入学生学号(3140-3146)和课程编号(1-"+max+")，中间以一个空格分开：");
	        		student_id=sc.next();
		        	course_id=sc.nextInt();
	        	}
	        	rs = stat.executeQuery("SELECT * FROM course_select WHERE student_id =\""+student_id+"\" and course_id="+course_id);
	        	if (!rs.next())
	        		println("没有选该课！");
	        	else{
	        		rs = stat.executeQuery("SELECT sum(credit) FROM course_select natural join course WHERE student_id =\""+student_id+"\"");
	        		rs.next();
	        		credit_sum = rs.getInt("sum(credit)");
	        		rs = stat.executeQuery("SELECT credit FROM course where course_id ="+course_id);
	        		rs.next();
	        		credit_sum-=rs.getInt("credit");
	        		if (credit_sum<min_credit){
	        			println("学分不足，退课之后学分 "+credit_sum+" 分，是否确定退课？（Y/N）");
	        			String ans = sc.next();
	        			if (ans.compareTo("Y")==0){ // 坚持退课
	        				sql = "DELETE FROM course_select WHERE student_id =\""+student_id+"\" and course_id="+course_id;
	        				stat.executeUpdate(sql);
	        				println("退课成功！当前学分："+credit_sum);
	        			}	
	        			else
	        				println("取消退课！");
	        		}
	        		else{
	        			sql = "DELETE FROM course_select WHERE student_id =\""+student_id+"\" and course_id="+course_id;
        				stat.executeUpdate(sql);
        				println("退课成功！当前学分："+credit_sum);
	        		}
	        	}
	        }
	        // 查询
	        if(str.compareTo("select")==0){
	        	String d1="Free",d2="Free",d3="Free",d4="Free",d5="Free";
	        	String id=null,ans;
	        	println("查询老师一周课表请输入\"1\"");
	        	println("查询学生一周课表请输入\"2\"");
	        	println("查询学生学分请输入\"3\"");
	        	int flag=0;
	        	ans=sc.next();
	        	//查询老师一周课表
	        	if (ans.compareTo("1")==0){
	        		flag=1;
	        		println("请输入老师工号：(1001-1004)");
	        		id=sc.next();
		        	while ((Integer.parseInt(id)>1004)||(Integer.parseInt(id)<1001)){
		        		println("输入有误，请重新输入老师工号：(1001-1004)");
		        		id=sc.next();
		        	}
	        		rs = stat.executeQuery("Select name from teacher where teacher_id = \""+id+"\"");
	        		rs.next();
	        		String name = rs.getString(1);
		        	println("=====Teacher "+name+"'s schedule=====");
		        	rs = stat.executeQuery("SELECT course_name,time,place FROM course WHERE teacher_id=\""+id+"\"");
	        	}
	        	//查询学生一周课表
	        	if(ans.compareTo("2")==0){
	        		flag=1;
	        		println("请输入学生学号：(3140-3146)");
	        		id=sc.next();
		        	while ((Integer.parseInt(id)>3146)||(Integer.parseInt(id)<3140)){
		        		println("输入有误，请重新输入学生学号：(3140-3146)");
		        		id=sc.next();
		        	}
	        		rs = stat.executeQuery("Select name from student where student_id = \""+id+"\"");
	        		rs.next();
	        		String name = rs.getString(1);
		        	println("=====Student "+name+"'s schedule=====");
		        	rs = stat.executeQuery("SELECT course_name,time,place FROM course natural join course_select WHERE student_id=\""+id+"\"");
	        	}
	        	if (flag==1) {
		        	while(rs.next()){
		        		if (rs.getString(2).compareTo("Monday")==0)
		        			d1 =rs.getString(1)+"\t"+rs.getString(3);
		        		if (rs.getString(2).compareTo("Tuesday")==0)
		        			d2 =rs.getString(1)+"\t"+rs.getString(3);
		        		if (rs.getString(2).compareTo("Wednesday")==0)
		        			d3 =rs.getString(1)+"\t"+rs.getString(3);
		        		if (rs.getString(2).compareTo("Thursday")==0)
		        			d4 =rs.getString(1)+"\t"+rs.getString(3);
		        		if (rs.getString(2).compareTo("Friday")==0)
		        			d5 =rs.getString(1)+"\t"+rs.getString(3);
		        	}
		        	println("周一   :"+d1);
		        	println("周二   :"+d2);
		        	println("周三   :"+d3);
		        	println("周四   :"+d4);
		        	println("周五   :"+d5);
	        	}
	        	//查询学生学分
	        	if(ans.compareTo("3")==0){
	        		rs = stat.executeQuery("select name,sum(credit) from course natural join course_select natural join student group by student_id");
	        		while(rs.next()){
	        			System.out.print("姓名: "+rs.getString(1)+"  学分: "+rs.getInt("sum(credit)"));
	        			if (rs.getInt("sum(credit)")<6)
	        				println("  学分不足！");
	        			else
	        				println("  学分足够！");	
	        		}
	        	}
	        	
	        }
	        // 增加新课程
	        if(str.compareTo("update")==0){
	        	println("请输出新增课程的名字、上课时间、上课地点、任课老师工号、学分");
	        	String new_name=sc.next();
	        	String new_time=sc.next();
	        	String new_place=sc.next();
	        	String new_id=sc.next();
	        	int credit=sc.nextInt();
	        	while ((Integer.parseInt(new_id)>1004)||(Integer.parseInt(new_id)<1001)){
	        		println("老师工号输入有误，请重新输入老师工号：(1001-1004)");
	        		//暂时没有实现其他排雷功能
	        	    new_id=sc.next();
	        	}
	        	if ((credit<=0)||(credit>6))
	        		println("学分不符合要求，新增课程失败！");
	        	else{
	        	rs = stat.executeQuery("select * from course where time=\""+new_time+"\" and place= \""+new_place+"\"");
	        	if (rs.next())
	        		println("课程教室冲突，新增课程失败！");
	        	else{
	        		rs = stat.executeQuery("select * from course where time=\""+new_time+"\" and teacher_id= \""+new_id+"\"");
	        		if (rs.next())
	        			println("老师时间冲突，新增课程失败！");
	        		else{
	        			sql = String.format(COURSE_INSERT_TEMPLATE, max+1,new_name,new_time,new_place,new_id,credit);
	    	            stat.executeUpdate(sql);
	    	            println("新增课程 "+new_name+" 成功！");
	        		
	        		}
	        	}
	        	}
	        }
	        println("请输入操作: (Add/Delete/Select/Update/Quit)");
	        str=sc.next();
	        //str=str.toLowerCase(); //并不需要转换
	        }
	        println("Byebye^_^");
	        sc.close();
	    }
	        /** close connection to exit */
	        connection.close();
	    }

	    private static void println(Object o) {
	        System.out.println(o);
	    }
}

