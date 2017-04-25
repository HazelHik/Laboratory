
#define GLEW_STATIC //静态链接glew。。unknown
#include <glew.h>   //管理OpenGL函数指针
#include <glfw3.h>  //用来创建窗口
#include <OpenGL/gl.h>
#include <OpenGL/glu.h>
#include <GLUT/glut.h>
#include <math.h>
#include <stdio.h>
#include <iostream>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#define M_PI 3.14159265358979323846
/*回调函数*/
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode);

/*顶点着色器*/
const GLchar* vertexShaderSource = "#version 330 core\n"
"layout (location = 0) in vec3 position;\n"  //用于管理 layout是一个标识
"uniform mat4 transform;\n"
"uniform float ratio;\n"
"void main()\n"
"{\n"
"float r1 = sqrt(0.5f * 0.5f * 3);\n"
"float r2 = sqrt(position.x * position.x + position.y * position.y + position.z * position.z);\n"
"float control = (r1/r2) * (ratio * (1-r2/r1) + r2/r1);\n"
"gl_Position = transform * vec4(control * position.x, control * position.y, control * position.z, 1.0f);\n"
"}\0";




/*片段着色器*/
const GLchar* fragmentShader1Source = "#version 330 core\n"
"out vec4 color;\n"
"uniform vec4 ourColor;\n"  //接受程序中改变的值？
"void main()\n"
"{\n"
"color = ourColor;\n"
"}\n\0";

const GLchar* fragmentShader2Source = "#version 330 core\n"
"out vec4 color;\n"
"void main()\n"
"{\n"
"color = vec4(1.0f, 1.0f, 1.0f, 1.0f);\n"
"}\n\0";
int main()
{
    /* GLFW */
    glfwInit();
    //基于OpenGL 3.3
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);// for mac OSX
    GLFWwindow* window = glfwCreateWindow(800, 800, "LearnOpenGL", nullptr, nullptr);
    if (window == nullptr)
    {
        printf("Failed to create GLFW window");
        glfwTerminate();
        return -1;
    }
    glfwMakeContextCurrent(window);
    glfwSetKeyCallback(window, key_callback);//回调函数在创建窗口之后注册
    /* GLEW */
    glewExperimental = GL_TRUE;
    if (glewInit() != GLEW_OK)
    {
        printf("Failed to initialize GLEW");
        return -1;
    }
    /* VIEWPORT */
    //告诉OpenGL渲染窗口的尺寸大小
    int width, height;
    glfwGetFramebufferSize(window, &width, &height); //从glfw获取width和height 实际可以比glfw的小
    glViewport(0, 0, width, height); //左下角位置（又一个坐标系）
    
    
    // 着色器对象
    GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
    GLuint fragmentShaderOrange = glCreateShader(GL_FRAGMENT_SHADER); // The first fragment shader that outputs the color orange
 
    GLuint shaderProgram = glCreateProgram();
    
    glShaderSource(vertexShader, 1, &vertexShaderSource, NULL);
    glCompileShader(vertexShader);
    
    glShaderSource(fragmentShaderOrange, 1, &fragmentShader1Source, NULL);
    glCompileShader(fragmentShaderOrange);
    // Link the first program object
    glAttachShader(shaderProgram, vertexShader);
    glAttachShader(shaderProgram, fragmentShaderOrange);
    glLinkProgram(shaderProgram);
    glDeleteShader(vertexShader); //链接完之后可以删掉
    glDeleteShader(fragmentShaderOrange);
    
    /*参数设置*/
    int frag=25;
    
    int p_size = 100/frag;
    int num = (frag+1)*(frag+1);
    GLfloat surface[num*4]; //front and back
    int point_num,i,j,h;
    point_num = 1;
    for(i=0;i<=frag;i++)
        for(j=0;j<=frag;j++){
            surface[point_num*3-3] = GLfloat(float(i)/frag-0.5);
            surface[point_num*3-2] = GLfloat(float(j)/frag-0.5);
            surface[point_num*3-1] = -0.5f;
            point_num++;
            }
    
    
    int index_num,a,b,c,d;
    GLint indices[num*6];
    index_num = 1;
    /*创建索引*/
    for (i=0;i<frag;i++)
        for(j=0;j<frag;j++){
            a = i*(frag+1)+j;b = a+1;
            c = a+frag+1;d = c+1;
            indices[index_num*3-3] = a;
            indices[index_num*3-2] = b;
            indices[index_num*3-1] = ((j+i)%2==0)?d:c;
            index_num++;
            indices[index_num*3-3] = c;
            indices[index_num*3-2] = d;
            indices[index_num*3-1] = ((i+j)%2==0)?a:b;
            index_num++;
            }
    
    GLuint VBO,VAO,EBO; //顶点缓冲对象 管理显卡内存数据
    glGenBuffers(1, &VBO);  //生成一个缓冲的意思（这个缓冲有一个独一无二的ID）
    glGenBuffers(1, &EBO);
    //    glGenBuffers(1, &EBO);  //索引缓冲对象(Element Buffer Object，EBO)
    glGenVertexArrays(1, &VAO);
    
    //顶点缓冲对象的缓冲类型是GL_ARRAY_BUFFER
    glBindVertexArray(VAO);//绑定VAO
    //把我们的顶点数组复制到一个顶点缓冲中，供OpenGL使用
    glBindBuffer(GL_ARRAY_BUFFER, VBO);  //把新创建的缓冲绑定到GL_ARRAY_BUFFER目标上
    glBufferData(GL_ARRAY_BUFFER, sizeof(surface),surface, GL_STATIC_DRAW);
    //把之前定义的顶点数据复制到缓冲内存中 sizeof用来测量vertices大小； GL_STATIC_DRAW表示数据几乎不会变，GL_DYNAMIC_DRAW：数据会被改变很多 GL_STREAM_DRAW ：数据每次绘制时都会改变，复制我们的索引数组到一个索引缓冲中，供OpenGL使用
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
    //现在已经把顶点数据给了GPU*/
    //设定顶点属性指针
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (GLvoid*)0);
    /*1.position顶点属性的位置值(Location)=0 */
    glEnableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    //解绑VAO（不是EBO！）
    glBindVertexArray(0);
    
    
    while(!glfwWindowShouldClose(window)) //游戏循环 检查glfwwindow是否被要求退出
    {
        glfwPollEvents();//函数检查有没有触发什么事件
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//设置清屏颜色 （状态设置函数
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT); //状态应用函数
        
        // 记得激活着色器
        glUseProgram(shaderProgram);
        
        // 更新uniform颜色
        GLfloat timeValue = glfwGetTime();
        GLfloat greenValue = (sin(timeValue) / 2) + 0.5; //uniform是全局变量
        GLfloat redValue = (sin(timeValue)/2);
        GLfloat blueValue = (sin(timeValue)/2)-0.5;
        GLint vertexColorLocation = glGetUniformLocation(shaderProgram, "ourColor"); //查询ourColor 的位置值
        glUniform4f(vertexColorLocation, redValue, greenValue, 0.5f, 1.0f); //更新uniform之前必须先useProgram
        
        GLfloat ratio = GLfloat((sin(timeValue)+1)/2);
        GLint CoeLoc = glGetUniformLocation(shaderProgram, "ratio");
        glUniform1f(CoeLoc,ratio);
        
        glBindVertexArray(VAO);
        
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        
        glm::mat4 transform;
        transform = glm::rotate(transform, (GLfloat)glfwGetTime() * 1.0f, glm::vec3(1.0f, 1.0f, 0.0f));
        
        //1
        transform = glm::translate(transform, glm::vec3(0.0f, 0.0f, 0.0f));//平移
        // Get matrix's uniform location and set matrix
        GLint transformLoc = glGetUniformLocation(shaderProgram, "transform");
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    
        glPointSize(p_size);
        
        //2
        transform = glm::rotate(transform, 1.5707963267948966f, glm::vec3(1.0f, 0.0f, 0.0f));
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        glUniform4f(vertexColorLocation, redValue, greenValue, 1.0f, 1.0f);
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_POINTS, 0, num);
        glPointSize(p_size);
        
        //3
        transform = glm::rotate(transform, 3.141592653589793f, glm::vec3(1.0f, 0.0f, 0.0f));
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        glUniform4f(vertexColorLocation, redValue,0.5, greenValue, 1.0f);
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_POINTS, 0, num);
        glPointSize(p_size);
        
        //4
        transform = glm::rotate(transform,4.71238898038469f, glm::vec3(1.0f, 0.0f, 0.0f));
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        glUniform4f(vertexColorLocation, redValue, 1.0f,greenValue, 1.0f);
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_POINTS, 0, num);
        glPointSize(p_size);
        
        //5
        transform = glm::rotate(transform,1.5707963267948966f, glm::vec3(0.0f, 1.0f, 0.0f));
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        glUniform4f(vertexColorLocation,0.5f, redValue, greenValue, 1.0f);
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_POINTS, 0, num);
        glPointSize(p_size);
        
        //6
        transform = glm::rotate(transform,3.141592653589793f, glm::vec3(0.0f, 1.0f, 0.0f));
        glUniformMatrix4fv(transformLoc, 1, GL_FALSE, glm::value_ptr(transform));
        glUniform4f(vertexColorLocation, 1.0f,redValue, greenValue, 1.0f);
        glDrawElements(GL_TRIANGLES,index_num*3-3, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_POINTS, 0, num);
        glPointSize(p_size);
        
     
        glBindVertexArray(0);
        glfwSwapBuffers(window);//交换颜色缓冲
        
    }
    glDeleteVertexArrays(1, &VAO);
    glDeleteBuffers(1, &VBO);
    glDeleteBuffers(1, &EBO);
    glfwTerminate(); //释放窗口的内存
    return 0;
}

/*一个键盘回调函数*/
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode)
{
    // 当用户按下ESC键,我们设置window窗口的WindowShouldClose属性为true
    // 关闭应用程序
    if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
        glfwSetWindowShouldClose(window, GL_TRUE);
}
