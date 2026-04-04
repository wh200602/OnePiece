package org.example.onepiece;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;

@MapperScan("org.example.onepiece.mapper")
@SpringBootApplication
public class OnePieceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnePieceApplication.class, args);
    }

    // 项目启动后自动执行该方法，测试数据库连接
    @Bean
    public CommandLineRunner testDbConnection(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                // 连接成功会打印以下信息
                System.out.println("✅ 数据库连接成功！");
                System.out.println("🔌 连接URL：" + conn.getMetaData().getURL());
                System.out.println("👤 数据库用户：" + conn.getMetaData().getUserName());
            } catch (Exception e) {
                // 连接失败会打印错误原因
                System.err.println("❌ 数据库连接失败！原因：" + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}