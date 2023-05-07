package com.bjpowernode.mvc.servlet;

import com.bjpowernode.mvc.servlet.exception.MoneyNotEnoughException;
import com.bjpowernode.mvc.servlet.exception.TransferFalseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * 处理用户转账
 * @author yk
 * @version 1.0
 * @since 1.0
 */
@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //获取用户输入
        String fromActno = request.getParameter("fromActno");
        String toActno = request.getParameter("toActno");
        double money = Double.parseDouble(request.getParameter("money"));

        //连接数据库
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;

        try {
            //1. 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2. 获取链接
            String url = "jdbc:mysql://localhost:3306/mvc";
            String user = "root";
            String password = "yangkai";
            conn = DriverManager.getConnection(url, user, password);
            //取消事务的自动提交
            conn.setAutoCommit(false);
            //3. 查看fromActno的余额是否充足
            String sql = "select balance from t_act where actno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, fromActno);
            rs = ps.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= money) {
                    //余额充足
                    //开始转账了
                    String sql2 = "update t_act set balance = balance - ? where actno = ?";
                    ps2 = conn.prepareStatement(sql2);
                    ps2.setDouble(1, money);
                    ps2.setString(2, fromActno);
                    int count = ps2.executeUpdate();

                    String sql3 = "update t_act set balance = balance + ? where actno = ?";
                    ps3 = conn.prepareStatement(sql3);
                    ps3.setDouble(1, money);
                    ps3.setString(2, toActno);
                    count += ps3.executeUpdate();
                    if (count != 2) {
                        //转账失败
                        throw new TransferFalseException("转账失败");
                    }
                } else {
                    //余额不足
                    throw new MoneyNotEnoughException("余额不足");
                }
                //提交事务
                conn.commit();
                out.print("转账成功");
            }
        } catch (Exception e) {
            //事务回滚
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
//            throw new RuntimeException(e);
            out.print(e.getMessage());
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps3 != null) {
                try {
                    ps3.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
