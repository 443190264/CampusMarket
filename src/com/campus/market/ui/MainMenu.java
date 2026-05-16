package com.campus.market.ui;

import com.campus.market.entity.*;
import com.campus.market.service.*;
import com.campus.market.service.impl.*;
import com.campus.market.dao.TransactionDao;
import com.campus.market.dao.impl.TransactionDaoImpl;
import com.campus.market.util.PasswordUtil;
import com.campus.market.exception.BusinessException;
import com.campus.market.exception.SystemException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private static Scanner scanner = new Scanner(System.in);
    private static int currentUserId = -1;
    private static boolean isAdmin = false;

    private static StudentService studentService = new StudentServiceImpl();
    private static ProductService productService = new ProductServiceImpl();
    private static BrowseHistoryService browseHistoryService = new BrowseHistoryServiceImpl();
    private static FavoriteService favoriteService = new FavoriteServiceImpl();
    private static TradeService tradeService = new TradeServiceImpl();
    private static LogService logService = new LogServiceImpl();
    private static TransactionDao transactionDao = new TransactionDaoImpl();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            boolean loggedIn = false;
            while (!loggedIn) {
                System.out.println("========== 校园二手交易平台 ==========");
                System.out.println("1. 登录");
                System.out.println("2. 注册");
                System.out.println("0. 退出程序");
                int choice = readInt("请选择", 0, 2);
                if (choice == 0) {
                    System.out.println("感谢使用，再见！");
                    System.exit(0);
                }
                if (choice == 2) {
                    registerNewStudent();
                } else {
                    Student s = login();
                    if (s != null) {
                        currentUserId = s.getId();
                        isAdmin = s.isAdmin();
                        System.out.println("登录成功！欢迎，" + s.getName() + "（ID:" + currentUserId + "）");
                        System.out.println("当前余额：" + s.getBalance() + "元");
                        if (isAdmin) {
                            System.out.println("（识别为管理员权限：可重置密码、查看所有日志）");
                        }
                        loggedIn = true;
                    } else {
                        System.out.println("登录失败，请重试或注册。");
                    }
                }
            }

            boolean backToLogin = false;
            while (!backToLogin) {
                showMainMenu();
                int maxOption = isAdmin ? 5 : 4;
                int menuChoice = readInt("请选择", 1, maxOption);
                if (!isAdmin) {
                    switch (menuChoice) {
                        case 1: studentManagementMenu(); break;
                        case 2: productMenu(); break;
                        case 3: viewMyLogs(); break;
                        case 4:
                            backToLogin = true;
                            System.out.println("已登出");
                            currentUserId = -1;
                            isAdmin = false;
                            break;
                    }
                } else {
                    switch (menuChoice) {
                        case 1: studentManagementMenu(); break;
                        case 2: productMenu(); break;
                        case 3: viewAllLogs(); break;
                        case 4: adminMenu(); break;
                        case 5:
                            backToLogin = true;
                            System.out.println("已登出");
                            currentUserId = -1;
                            isAdmin = false;
                            break;
                    }
                }
                if (currentUserId == -1) backToLogin = true;
            }
        }
        scanner.close();
    }

    // 注册
    private static void registerNewStudent() {
        System.out.println("=== 新用户注册（输入0可取消）===");
        System.out.print("学号: ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) { System.out.println("已取消注册。"); return; }
        System.out.print("姓名: ");
        String name = scanner.nextLine();
        if (name.equals("0")) return;
        System.out.print("电话: ");
        String phone = scanner.nextLine();
        if (phone.equals("0")) return;
        System.out.print("密码: ");
        String pwd = scanner.nextLine();
        if (pwd.equals("0")) return;

        Student s = new Student();
        s.setStudentId(sid);
        s.setName(name);
        s.setPhone(phone);
        s.setBalance(BigDecimal.ZERO);
        s.setPassword(pwd);

        try {
            boolean ok = studentService.register(s);
            if (ok) {
                System.out.println("注册成功！您的学生ID是：" + s.getId());
                System.out.println("请记住学号和密码，下次登录使用。");
            }
        } catch (BusinessException e) {
            System.out.println("注册失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }

    // 登录
    private static Student login() {
        System.out.println("=== 登录（输入0取消）===");
        System.out.print("学号: ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) return null;
        System.out.print("密码: ");
        String pwd = scanner.nextLine();
        if (pwd.equals("0")) return null;

        try {
            return studentService.login(sid, pwd);
        } catch (BusinessException e) {
            System.out.println("登录失败：" + e.getMessage());
            return null;
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
            return null;
        }
    }

    //主菜单显示
    private static void showMainMenu() {
        System.out.println("\n========== 主菜单 ==========");
        System.out.println("1. 学生管理");
        System.out.println("2. 商品管理");
        if (isAdmin) {
            System.out.println("3. 操作日志");
            System.out.println("4. 管理员工具");
            System.out.println("5. 登出");
        } else {
            System.out.println("3. 我的操作日志");
            System.out.println("4. 登出");
        }
        System.out.println("===========================");
    }

    // 学生管理
    private static void studentManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- 学生管理 ---");
            System.out.println("1. 查看当前账号信息");
            System.out.println("2. 修改我的信息");
            System.out.println("3. 删除我的账号");
            System.out.println("4. 查询学生（ID/学号/模糊姓名）");
            System.out.println("5. 充值");
            System.out.println("6. 返回主菜单");
            int choice = readInt("请选择", 1, 6);
            switch (choice) {
                case 1: showCurrentStudentInfo(); break;
                case 2: updateStudent(); break;
                case 3: deleteStudent(); if (currentUserId == -1) back = true; break;
                case 4: searchStudent(); break;
                case 5: recharge(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void showCurrentStudentInfo() {
        if (currentUserId <= 0) { System.out.println("未登录"); return; }
        Student s = studentService.getById(currentUserId);
        if (s == null) { System.out.println("用户不存在"); currentUserId = -1; return; }
        System.out.println("┌──────────────┬────────────────────┐");
        System.out.printf("│ 学生ID       │ %-18d │\n", s.getId());
        System.out.printf("│ 学号         │ %-18s │\n", s.getStudentId());
        System.out.printf("│ 姓名         │ %-18s │\n", s.getName());
        System.out.printf("│ 电话         │ %-18s │\n", s.getPhone());
        System.out.printf("│ 余额         │ %-18.2f元 │\n", s.getBalance());
        System.out.printf("│ 管理员       │ %-18s │\n", s.isAdmin() ? "是" : "否");
        System.out.println("└──────────────┴────────────────────┘");
    }

    // 修改学生信息
    private static void updateStudent() {
        if (currentUserId <= 0) { System.out.println("请先登录"); return; }
        Student s = studentService.getById(currentUserId);
        if (s == null) { System.out.println("用户不存在"); return; }
        System.out.println("当前信息: " + s);
        System.out.println("（输入0可取消修改）");
        System.out.print("新姓名（回车不变）: ");
        String name = scanner.nextLine();
        if (name.equals("0")) { System.out.println("已取消修改"); return; }
        if (!name.isEmpty()) s.setName(name);
        System.out.print("新电话（回车不变）: ");
        String phone = scanner.nextLine();
        if (phone.equals("0")) return;
        if (!phone.isEmpty()) s.setPhone(phone);
        System.out.print("新密码（回车不变，输入0取消）: ");
        String pwd = scanner.nextLine();
        if (pwd.equals("0")) return;
        if (!pwd.isEmpty()) {
            String newSalt = PasswordUtil.generateSalt();
            String newHash = PasswordUtil.hashPassword(pwd, newSalt);
            s.setSalt(newSalt);
            s.setPassword(newHash);
        }
        System.out.println("提示：余额只能通过充值修改。");

        try {
            boolean ok = studentService.modifyInfo(s);
            System.out.println(ok ? "修改成功" : "修改失败");
        } catch (BusinessException e) {
            System.out.println("修改失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }

    private static void deleteStudent() {
        if (currentUserId <= 0) { System.out.println("请先登录"); return; }
        System.out.print("确认删除当前登录的学生？(y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            try {
                boolean ok = studentService.removeStudent(currentUserId);
                if (ok) { System.out.println("删除成功，您已被登出。"); currentUserId = -1; isAdmin = false; }
                else System.out.println("删除失败");
            } catch (BusinessException e) {
                System.out.println("删除失败：" + e.getMessage());
            } catch (SystemException e) {
                System.out.println("系统错误，请稍后再试");
                e.printStackTrace();
            }
        }
    }

    private static void searchStudent() {
        System.out.println("1. 按ID查询  2. 按学号查询  3. 按姓名模糊查询");
        int type = readInt("选择", 1, 3);
        if (type == 1) {
            int id = readInt("输入学生ID", 1, Integer.MAX_VALUE);
            Student s = studentService.getById(id);
            if (s != null) System.out.println(s);
            else System.out.println("未找到学生，ID=" + id);
        } else if (type == 2) {
            System.out.print("输入学号: "); String sid = scanner.nextLine();
            Student s = studentService.getByStudentId(sid);
            if (s != null) System.out.println(s);
            else System.out.println("未找到学生，学号=" + sid);
        } else {
            System.out.print("输入姓名关键字: "); String kw = scanner.nextLine();
            List<Student> list = studentService.searchByName(kw);
            if (list.isEmpty()) System.out.println("未找到姓名包含 '" + kw + "' 的学生");
            else list.forEach(System.out::println);
        }
    }

    // 充值
    private static void recharge() {
        if (currentUserId <= 0) { System.out.println("请先登录"); return; }
        System.out.println("（输入0可取消）");
        System.out.print("充值金额（正数）: ");
        String amountStr = scanner.nextLine();
        if (amountStr.equals("0")) { System.out.println("已取消充值"); return; }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("无效金额");
            return;
        }

        try {
            boolean ok = studentService.recharge(currentUserId, amount);
            if (ok) System.out.println("充值成功，当前余额：" + studentService.getById(currentUserId).getBalance() + "元");
            else System.out.println("充值失败");
        } catch (BusinessException e) {
            System.out.println("充值失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }

    // 商品管理
    private static void productMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- 商品管理 ---");
            System.out.println("1. 发布商品");
            System.out.println("2. 查看我的商品（可上架/下架）");
            System.out.println("3. 搜索商品（分页、可购买/收藏/查看详情）");
            System.out.println("4. 我的交易记录");
            System.out.println("5. 我的收藏");
            System.out.println("6. 我的浏览历史");
            System.out.println("7. 返回主菜单");
            int choice = readInt("请选择", 1, 7);
            switch (choice) {
                case 1: publishProduct(); break;
                case 2: manageMyProducts(); break;
                case 3: searchAndInteractPaged(); break;
                case 4: transactionRecordMenu(); break;
                case 5: favoriteMenu(); break;
                case 6: browseHistoryMenu(); break;
                case 7: back = true; break;
            }
        }
    }

    // 发布商品
    private static void publishProduct() {
        if (currentUserId <= 0) { System.out.println("请登录后发布商品"); return; }
        System.out.println("（输入0可取消发布）");
        System.out.print("标题: ");
        String title = scanner.nextLine();
        if ("0".equals(title)) { System.out.println("已取消"); return; }
        System.out.print("描述: ");
        String desc = scanner.nextLine();
        if ("0".equals(desc)) return;
        System.out.print("价格（纯数字）: ");
        BigDecimal price = null;
        while (price == null) {
            String priceStr = scanner.nextLine();
            if ("0".equals(priceStr)) { System.out.println("已取消"); return; }
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) System.out.print("价格不能为负数，重新输入（0取消）: ");
            } catch (NumberFormatException e) {
                System.out.print("格式错误，请输入数字（0取消）: ");
            }
        }
        System.out.print("分类: ");
        String cate = scanner.nextLine();
        if ("0".equals(cate)) return;
        Product p = new Product();
        p.setSellerId(currentUserId);
        p.setTitle(title);
        p.setDescription(desc);
        p.setPrice(price);
        p.setCategory(cate);
        p.setStatus("ON_SALE");

        try {
            boolean ok = productService.publish(p);
            System.out.println(ok ? "发布成功，商品ID=" + p.getId() : "发布失败");
        } catch (BusinessException e) {
            System.out.println("发布失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }

    private static void manageMyProducts() {
        if (currentUserId <= 0) { System.out.println("请登录"); return; }

        int pageSize = 5;
        int pageNum = 1;

        while (true) {
            List<Product> myProducts = productService.getProductsBySellerWithPage(currentUserId, pageNum, pageSize);
            int total = productService.getProductCountBySeller(currentUserId);
            int totalPages = (int) Math.ceil((double) total / pageSize);

            if (myProducts.isEmpty() && pageNum == 1) {
                System.out.println("您还没有发布任何商品");
                break;
            }

            System.out.println("\n===== 我的商品列表（第 " + pageNum + " / " + totalPages + " 页）=====");
            System.out.printf("%-4s %-20s %-10s %-10s %-10s%n", "序号", "标题", "价格", "状态", "商品ID");
            for (int i = 0; i < myProducts.size(); i++) {
                Product p = myProducts.get(i);
                System.out.printf("%-4d %-20s %-10.2f元 %-10s %-10d%n",
                        (i+1), p.getTitle(), p.getPrice(), p.getStatus(), p.getId());
            }

            System.out.println("\n操作: [序号]修改/上架/下架, [N]下一页, [P]上一页, [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) {
                break;
            } else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else {
                try {
                    int idx = Integer.parseInt(cmd);
                    if (idx >= 1 && idx <= myProducts.size()) {
                        Product p = myProducts.get(idx-1);
                        System.out.println("\n请选择操作：");
                        System.out.println("1. 修改商品信息");
                        System.out.println("2. 上架/下架");
                        System.out.println("0. 返回");
                        int action = readInt("请选择", 0, 2);
                        if (action == 1) {
                            modifyProductInfo(p);
                            myProducts = productService.getProductsBySellerWithPage(currentUserId, pageNum, pageSize);
                        } else if (action == 2) {
                            System.out.println("当前状态：" + p.getStatus());
                            System.out.print("操作(1-上架 2-下架): ");
                            int op = readInt("", 1, 2);
                            try {
                                boolean ok = (op == 1) ? productService.onShelf(p.getId(), currentUserId)
                                        : productService.offShelf(p.getId(), currentUserId);
                                System.out.println(ok ? "操作成功" : "操作失败");
                            } catch (BusinessException e) {
                                System.out.println("操作失败：" + e.getMessage());
                            } catch (SystemException e) {
                                System.out.println("系统错误，请稍后再试");
                                e.printStackTrace();
                            }
                            myProducts = productService.getProductsBySellerWithPage(currentUserId, pageNum, pageSize);
                        }
                    } else {
                        System.out.println("序号超出范围");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效输入");
                }
            }
        }
    }

    //修改商品信息
    private static void modifyProductInfo(Product p) {
        System.out.println("\n当前商品信息：");
        System.out.println("标题：" + p.getTitle());
        System.out.println("描述：" + p.getDescription());
        System.out.println("价格：" + p.getPrice() + "元");
        System.out.println("分类：" + p.getCategory());

        System.out.println("\n（输入0可取消修改）");

        System.out.print("新标题（回车不变）: ");
        String title = scanner.nextLine();
        if (title.equals("0")) { System.out.println("已取消修改"); return; }
        if (!title.trim().isEmpty()) p.setTitle(title);

        System.out.print("新描述（回车不变）: ");
        String desc = scanner.nextLine();
        if (desc.equals("0")) { System.out.println("已取消修改"); return; }
        if (!desc.trim().isEmpty()) p.setDescription(desc);

        System.out.print("新价格（回车不变）: ");
        String priceStr = scanner.nextLine();
        if (priceStr.equals("0")) { System.out.println("已取消修改"); return; }
        if (!priceStr.trim().isEmpty()) {
            try {
                BigDecimal newPrice = new BigDecimal(priceStr);
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("价格不能为负数，修改取消");
                    return;
                }
                p.setPrice(newPrice);
            } catch (NumberFormatException e) {
                System.out.println("价格格式错误，修改取消");
                return;
            }
        }

        System.out.print("新分类（回车不变）: ");
        String cate = scanner.nextLine();
        if (cate.equals("0")) { System.out.println("已取消修改"); return; }
        if (!cate.trim().isEmpty()) p.setCategory(cate);

        try {
            boolean ok = productService.modify(p, currentUserId);
            System.out.println(ok ? "修改成功" : "修改失败");
        } catch (BusinessException e) {
            System.out.println("修改失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }

    // 分页搜索商品
    private static void searchAndInteractPaged() {
        System.out.print("关键字（标题模糊，回车忽略）: ");
        String kw = scanner.nextLine(); if (kw.trim().isEmpty()) kw = null;
        System.out.print("分类（回车忽略）: ");
        String cate = scanner.nextLine(); if (cate.trim().isEmpty()) cate = null;
        System.out.print("最低价（回车忽略）: ");
        String minStr = scanner.nextLine();
        BigDecimal min = minStr.isEmpty() ? null : new BigDecimal(minStr);
        System.out.print("最高价（回车忽略）: ");
        String maxStr = scanner.nextLine();
        BigDecimal max = maxStr.isEmpty() ? null : new BigDecimal(maxStr);
        System.out.print("状态(ON_SALE/OFF_SALE/SOLD，回车忽略): ");
        String status = scanner.nextLine(); if (status.trim().isEmpty()) status = null;
        System.out.print("排序(price_asc/price_desc/time_desc，回车默认时间倒序): ");
        String sort = scanner.nextLine(); if (sort.trim().isEmpty()) sort = null;

        int pageSize = 5;
        int pageNum = 1;
        int excludeUserId = (currentUserId > 0) ? currentUserId : -1;

        while (true) {
            List<Product> list = productService.searchProductsByPage(kw, cate, min, max, status, sort, pageNum, pageSize, excludeUserId);
            int total = productService.getProductCount(kw, cate, min, max, status, excludeUserId);
            int totalPages = (int) Math.ceil((double) total / pageSize);

            if (list.isEmpty()) {
                System.out.println("没有找到符合条件的商品" + (excludeUserId > 0 ? "（已排除您自己发布的商品）" : ""));
                break;
            }

            System.out.println("\n===== 搜索结果（第 " + pageNum + " / " + totalPages + " 页）=====");
            System.out.printf("%-4s %-20s %-10s %-10s%n", "序号", "标题", "价格", "状态");
            for (int i = 0; i < list.size(); i++) {
                Product p = list.get(i);
                System.out.printf("%-4d %-20s %-10.2f元 %-10s%n", (i + 1), p.getTitle(), p.getPrice(), p.getStatus());
            }
            System.out.println("\n操作: [序号]查看/购买/收藏, [N]下一页, [P]上一页, [E]导出当前页, [A]导出全部, [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) {
                break;
            } else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else if (cmd.equalsIgnoreCase("E")) {
                exportToCSV(list);
            } else if (cmd.equalsIgnoreCase("A")) {
                List<Product> allProducts = productService.searchProducts(kw, cate, min, max, status, sort, excludeUserId);
                if (allProducts.isEmpty()) {
                    System.out.println("没有数据可导出");
                } else {
                    exportToCSV(allProducts);
                    System.out.println("导出成功，共 " + allProducts.size() + " 条商品");
                }
            } else {
                try {
                    int idx = Integer.parseInt(cmd);
                    if (idx >= 1 && idx <= list.size()) {
                        Product selected = list.get(idx - 1);
                        if (currentUserId > 0) {
                            browseHistoryService.recordBrowse(currentUserId, selected.getId());
                        }
                        viewProductDetail(selected);
                        while (true) {
                            System.out.println("\n请选择操作：1.购买 2.收藏 0.返回");
                            int op = readInt("请选择", 0, 2);
                            if (op == 0) break;
                            if (op == 1) {
                                if (currentUserId <= 0) System.out.println("请先登录");
                                else if (!"ON_SALE".equals(selected.getStatus())) System.out.println("商品已下架或已售出");
                                else {
                                    try {
                                        boolean buyOk = tradeService.buyProduct(currentUserId, selected.getId());
                                        System.out.println(buyOk ? "购买成功" : "购买失败");
                                    } catch (BusinessException e) {
                                        System.out.println("购买失败：" + e.getMessage());
                                    } catch (SystemException e) {
                                        System.out.println("系统错误，请稍后再试");
                                        e.printStackTrace();
                                    }
                                }
                            } else if (op == 2) {
                                if (currentUserId <= 0) System.out.println("请先登录");
                                else {
                                    try {
                                        boolean added = favoriteService.addFavorite(currentUserId, selected.getId());
                                        System.out.println(added ? "收藏成功" : "收藏失败（可能已收藏）");
                                    } catch (BusinessException e) {
                                        System.out.println("收藏失败：" + e.getMessage());
                                    } catch (SystemException e) {
                                        System.out.println("系统错误，请稍后再试");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("序号超出范围");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效输入");
                }
            }
        }
    }

    private static void transactionRecordMenu() {
        if (currentUserId <= 0) { System.out.println("请登录后查看交易记录"); return; }
        boolean back = false;
        while (!back) {
            System.out.println("\n--- 我的交易记录 ---");
            System.out.println("1. 我的买入记录（可退货）");
            System.out.println("2. 我的卖出记录（仅查看）");
            System.out.println("3. 返回上一级");
            int choice = readInt("请选择", 1, 3);
            switch (choice) {
                case 1: viewPurchasedProducts(); break;
                case 2: viewSoldProductsOnly(); break;
                case 3: back = true; break;
            }
        }
    }

    private static void viewPurchasedProducts() {
        List<TransactionRecord> all = transactionDao.findByBuyerId(currentUserId);
        List<TransactionRecord> completed = new ArrayList<>();
        for (TransactionRecord t : all) if ("COMPLETED".equals(t.getStatus())) completed.add(t);
        if (completed.isEmpty()) { System.out.println("您还没有购买过任何商品"); return; }
        while (true) {
            System.out.println("\n===== 已购入商品列表（买家视角） =====");
            System.out.printf("%-4s %-20s %-10s %-20s %-10s%n", "序号", "商品标题", "价格", "交易时间", "交易ID");
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < completed.size(); i++) {
                TransactionRecord t = completed.get(i);
                Product p = productService.getProductById(t.getProductId());
                String title = (p != null) ? p.getTitle() : "已删除商品";
                System.out.printf("%-4d %-20s %-10.2f元 %-20s %-10d%n", (i+1), title, t.getAmount(), t.getTradeTime().toString().substring(0,19), t.getId());
                ids.add(t.getId());
            }
            System.out.println("0. 返回上一级");
            System.out.print("请输入序号选择要退货的商品（或0返回）: ");
            int idx = readInt("", 0, completed.size());
            if (idx == 0) break;
            try {
                boolean ret = tradeService.returnProduct(ids.get(idx-1), currentUserId);
                System.out.println(ret ? "退货成功" : "退货失败");
            } catch (BusinessException e) {
                System.out.println("退货失败：" + e.getMessage());
            } catch (SystemException e) {
                System.out.println("系统错误，请稍后再试");
                e.printStackTrace();
            }
            all = transactionDao.findByBuyerId(currentUserId);
            completed.clear();
            for (TransactionRecord t : all) if ("COMPLETED".equals(t.getStatus())) completed.add(t);
            if (completed.isEmpty()) { System.out.println("您已没有可退货的商品"); break; }
        }
    }

    private static void viewSoldProductsOnly() {
        List<TransactionRecord> all = transactionDao.findBySellerId(currentUserId);
        List<TransactionRecord> completed = new ArrayList<>();
        for (TransactionRecord t : all) if ("COMPLETED".equals(t.getStatus())) completed.add(t);
        if (completed.isEmpty()) { System.out.println("您还没有卖出任何商品"); return; }
        System.out.println("\n===== 卖出的商品列表（卖家视角，仅查看）=====");
        System.out.printf("%-4s %-20s %-10s %-20s %-10s%n", "序号", "商品标题", "价格", "交易时间", "交易ID");
        for (int i = 0; i < completed.size(); i++) {
            TransactionRecord t = completed.get(i);
            Product p = productService.getProductById(t.getProductId());
            String title = (p != null) ? p.getTitle() : "已删除商品";
            System.out.printf("%-4d %-20s %-10.2f元 %-20s %-10d%n", (i+1), title, t.getAmount(), t.getTradeTime().toString().substring(0,19), t.getId());
        }
        System.out.print("按回车键继续...");
        scanner.nextLine();
    }

    private static void viewProductDetail(Product p) {
        System.out.println("=== 商品详情 ===");
        System.out.println("ID: " + p.getId() + " | 标题: " + p.getTitle());
        System.out.println("描述: " + p.getDescription());
        System.out.println("价格: " + p.getPrice() + "元 | 分类: " + p.getCategory());
        System.out.println("状态: " + p.getStatus() + " | 卖家ID: " + p.getSellerId());
        System.out.println("发布时间: " + p.getPublishTime());
    }

    //  浏览历史
    private static void browseHistoryMenu() {
        if (currentUserId <= 0) { System.out.println("请登录后查看浏览历史"); return; }

        int pageSize = 10;
        int pageNum = 1;

        while (true) {
            List<BrowseHistory> list = browseHistoryService.getHistoryWithPage(currentUserId, pageNum, pageSize);
            int total = browseHistoryService.getHistoryCount(currentUserId);
            int totalPages = (int) Math.ceil((double) total / pageSize);

            if (list.isEmpty() && pageNum == 1) {
                System.out.println("暂无浏览历史记录");
                System.out.print("按回车键返回主菜单...");
                scanner.nextLine();
                break;
            }

            System.out.println("\n--- 我的浏览历史（第 " + pageNum + " / " + totalPages + " 页）---");
            System.out.printf("%-4s %-20s %-20s%n", "序号", "商品标题", "浏览时间");
            for (int i = 0; i < list.size(); i++) {
                BrowseHistory bh = list.get(i);
                Product p = productService.getProductById(bh.getProductId());
                String title = (p != null) ? p.getTitle() : "已删除商品";
                System.out.printf("%-4d %-20s %-20s%n", (i+1), title, bh.getBrowseTime().toString().substring(0,19));
            }
            System.out.println("\n操作: [序号]删除记录, [N]下一页, [P]上一页, [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) {
                break;
            } else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else {
                try {
                    int idx = Integer.parseInt(cmd);
                    if (idx >= 1 && idx <= list.size()) {
                        try {
                            boolean del = browseHistoryService.deleteHistoryItem(list.get(idx-1).getId());
                            System.out.println(del ? "删除成功" : "删除失败");
                        } catch (BusinessException e) {
                            System.out.println("删除失败：" + e.getMessage());
                        } catch (SystemException e) {
                            System.out.println("系统错误，请稍后再试");
                            e.printStackTrace();
                        }
                        if (list.size() == 1 && pageNum > 1) pageNum--;
                    } else {
                        System.out.println("序号超出范围");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效输入");
                }
            }
        }
    }

    // 收藏管理
    private static void favoriteMenu() {
        if (currentUserId <= 0) { System.out.println("请登录后管理收藏"); return; }
        boolean back = false;
        while (!back) {
            System.out.println("\n--- 我的收藏 ---");
            System.out.println("1. 查看我的收藏列表");
            System.out.println("2. 收藏商品");
            System.out.println("3. 返回主菜单");
            int op = readInt("请选择", 1, 3);
            switch (op) {
                case 1:
                    viewAndManageFavorites();
                    break;
                case 2:
                    addFavoriteWithSearch();
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private static void viewAndManageFavorites() {
        List<Favorite> favList = favoriteService.getMyFavorites(currentUserId);
        if (favList.isEmpty()) {
            System.out.println("暂无收藏");
            System.out.print("按回车键继续...");
            scanner.nextLine();
            return;
        }

        while (true) {
            System.out.println("\n===== 我的收藏列表 =====");
            for (int i = 0; i < favList.size(); i++) {
                Favorite f = favList.get(i);
                Product p = productService.getProductById(f.getProductId());
                String title = (p != null) ? p.getTitle() : "已删除商品";
                String favTime = f.getFavTime().toString().substring(0,19);
                System.out.println("[" + (i+1) + "] " + title + " | 收藏时间: " + favTime + " | 商品ID: " + f.getProductId());
            }
            System.out.println("\n输入 [序号] 查看/购买/取消收藏, 输入 [Q] 退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) break;
            try {
                int idx = Integer.parseInt(cmd);
                if (idx >= 1 && idx <= favList.size()) {
                    int productId = favList.get(idx-1).getProductId();
                    Product p = productService.getProductById(productId);
                    if (p == null) { System.out.println("商品已不存在"); continue; }
                    if (currentUserId > 0) browseHistoryService.recordBrowse(currentUserId, productId);
                    viewProductDetail(p);
                    while (true) {
                        System.out.println("\n请选择操作：");
                        System.out.println("1. 购买此商品");
                        System.out.println("2. 取消收藏");
                        System.out.println("0. 返回收藏列表");
                        int action = readInt("请选择", 0, 2);
                        if (action == 0) break;
                        if (action == 1) {
                            if (!"ON_SALE".equals(p.getStatus())) {
                                System.out.println("商品已下架或已售出，无法购买");
                            } else {
                                try {
                                    boolean buyOk = tradeService.buyProduct(currentUserId, productId);
                                    System.out.println(buyOk ? "购买成功" : "购买失败");
                                } catch (BusinessException e) {
                                    System.out.println("购买失败：" + e.getMessage());
                                } catch (SystemException e) {
                                    System.out.println("系统错误，请稍后再试");
                                    e.printStackTrace();
                                }
                            }
                            favList = favoriteService.getMyFavorites(currentUserId);
                            if (favList.isEmpty()) { System.out.println("收藏列表已空，返回上一级"); return; }
                            break;
                        } else if (action == 2) {
                            try {
                                boolean removed = favoriteService.cancelFavorite(currentUserId, productId);
                                if (removed) {
                                    System.out.println("已取消收藏");
                                    favList = favoriteService.getMyFavorites(currentUserId);
                                    if (favList.isEmpty()) { System.out.println("收藏列表已空，返回上一级"); return; }
                                    break;
                                } else {
                                    System.out.println("取消收藏失败");
                                }
                            } catch (BusinessException e) {
                                System.out.println("取消失败：" + e.getMessage());
                            } catch (SystemException e) {
                                System.out.println("系统错误，请稍后再试");
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    System.out.println("序号超出范围");
                }
            } catch (NumberFormatException e) {
                System.out.println("无效输入");
            }
        }
    }

    private static void addFavoriteWithSearch() {
        System.out.println("（输入0可取消）");
        System.out.print("关键字（标题模糊，回车忽略）: ");
        String kw = scanner.nextLine();
        if ("0".equals(kw)) { System.out.println("已取消"); return; }
        if (kw.trim().isEmpty()) kw = null;
        System.out.print("分类（回车忽略）: ");
        String cate = scanner.nextLine();
        if ("0".equals(cate)) { System.out.println("已取消"); return; }
        if (cate.trim().isEmpty()) cate = null;
        System.out.print("最低价（回车忽略）: ");
        String minStr = scanner.nextLine();
        if ("0".equals(minStr)) { System.out.println("已取消"); return; }
        BigDecimal min = minStr.isEmpty() ? null : new BigDecimal(minStr);
        System.out.print("最高价（回车忽略）: ");
        String maxStr = scanner.nextLine();
        if ("0".equals(maxStr)) { System.out.println("已取消"); return; }
        BigDecimal max = maxStr.isEmpty() ? null : new BigDecimal(maxStr);
        int excludeUserId = (currentUserId > 0) ? currentUserId : -1;
        List<Product> list = productService.searchProducts(kw, cate, min, max, null, null, excludeUserId);
        if (list.isEmpty()) { System.out.println("没有找到符合条件的商品"); return; }
        int idx = displayProductList(list);
        if (idx == -1) return;
        Product p = list.get(idx);
        System.out.println("\n请选择操作：1. 直接收藏  2. 查看详情  0. 取消");
        int op = readInt("", 0, 2);
        if (op == 1) {
            try {
                boolean added = favoriteService.addFavorite(currentUserId, p.getId());
                System.out.println(added ? "收藏成功" : "收藏失败");
            } catch (BusinessException e) {
                System.out.println("收藏失败：" + e.getMessage());
            } catch (SystemException e) {
                System.out.println("系统错误，请稍后再试");
                e.printStackTrace();
            }
        } else if (op == 2) {
            if (currentUserId > 0) browseHistoryService.recordBrowse(currentUserId, p.getId());
            viewProductDetail(p);
            System.out.print("是否收藏此商品？(y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                try {
                    boolean added = favoriteService.addFavorite(currentUserId, p.getId());
                    System.out.println(added ? "收藏成功" : "收藏失败");
                } catch (BusinessException e) {
                    System.out.println("收藏失败：" + e.getMessage());
                } catch (SystemException e) {
                    System.out.println("系统错误，请稍后再试");
                    e.printStackTrace();
                }
            }
        }
    }

    //  操作日志
    private static void viewMyLogs() {
        int pageSize = 10;
        int pageNum = 1;
        while (true) {
            List<OperationLog> logs = logService.getLogsByUserWithPage(currentUserId, pageNum, pageSize);
            int total = logService.getUserLogCount(currentUserId);
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (logs.isEmpty() && pageNum == 1) { System.out.println("暂无操作日志"); break; }
            System.out.println("\n===== 您的操作日志（第 " + pageNum + " / " + totalPages + " 页）=====");
            System.out.println("操作ID | 动作 | 详情 | 时间");
            System.out.println("-------|------|------|-------------------");
            for (OperationLog log : logs) {
                System.out.printf("%-6d | %-10s | %-20s | %s%n",
                        log.getId(), log.getAction(),
                        log.getDetail() != null && log.getDetail().length() > 20 ? log.getDetail().substring(0,20)+"..." : log.getDetail(),
                        log.getLogTime());
            }
            System.out.println("\n[N]下一页 [P]上一页 [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) break;
            else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else {
                System.out.println("无效输入");
            }
        }
    }

    private static void viewAllLogs() {
        System.out.println("\n--- 查看操作日志 ---");
        System.out.println("1. 查看所有日志");
        System.out.println("2. 按学生ID筛选");
        System.out.println("0. 返回");
        int choice = readInt("请选择", 0, 2);
        if (choice == 0) return;
        if (choice == 2) {
            System.out.print("请输入学生ID: ");
            int studentId;
            try {
                studentId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("无效的学生ID");
                return;
            }
            viewLogsByUserId(studentId);
        } else {
            viewAllLogsPaged();
        }
    }

    private static void viewAllLogsPaged() {
        int pageSize = 10;
        int pageNum = 1;
        while (true) {
            List<OperationLog> logs = logService.getAllLogsWithPage(pageNum, pageSize);
            int total = logService.getTotalLogCount();
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (logs.isEmpty() && pageNum == 1) { System.out.println("暂无操作日志"); break; }
            System.out.println("\n===== 所有操作日志（第 " + pageNum + " / " + totalPages + " 页）=====");
            System.out.println("操作ID | 操作人 | 动作 | 详情 | 时间");
            System.out.println("-------|--------|------|------|-------------------");
            for (OperationLog log : logs) {
                String operator = log.getOperatorId() == null ? "系统" : String.valueOf(log.getOperatorId());
                System.out.printf("%-6d | %-6s | %-10s | %-20s | %s%n",
                        log.getId(), operator, log.getAction(),
                        log.getDetail() != null && log.getDetail().length() > 20 ? log.getDetail().substring(0,20)+"..." : log.getDetail(),
                        log.getLogTime());
            }
            System.out.println("\n[N]下一页 [P]上一页 [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) break;
            else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else {
                System.out.println("无效输入");
            }
        }
    }

    private static void viewLogsByUserId(int studentId) {
        Student student = studentService.getById(studentId);
        if (student == null) {
            System.out.println("学生ID不存在：" + studentId);
            System.out.print("按回车键继续...");
            scanner.nextLine();
            return;
        }
        System.out.println("查看学生：" + student.getName() + "（学号：" + student.getStudentId() + "）的操作日志");
        int pageSize = 10;
        int pageNum = 1;
        while (true) {
            List<OperationLog> logs = logService.getLogsByUserWithPage(studentId, pageNum, pageSize);
            int total = logService.getUserLogCount(studentId);
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (logs.isEmpty() && pageNum == 1) { System.out.println("该学生暂无操作日志"); break; }
            System.out.println("\n===== 学生 " + student.getName() + " 的操作日志（第 " + pageNum + " / " + totalPages + " 页）=====");
            System.out.println("操作ID | 操作人 | 动作 | 详情 | 时间");
            System.out.println("-------|--------|------|------|-------------------");
            for (OperationLog log : logs) {
                String operator = log.getOperatorId() == null ? "系统" : String.valueOf(log.getOperatorId());
                System.out.printf("%-6d | %-6s | %-10s | %-20s | %s%n",
                        log.getId(), operator, log.getAction(),
                        log.getDetail() != null && log.getDetail().length() > 20 ? log.getDetail().substring(0,20)+"..." : log.getDetail(),
                        log.getLogTime());
            }
            System.out.println("\n[N]下一页 [P]上一页 [Q]退出");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("Q")) break;
            else if (cmd.equalsIgnoreCase("N")) {
                if (pageNum < totalPages) pageNum++;
                else System.out.println("已是最后一页");
            } else if (cmd.equalsIgnoreCase("P")) {
                if (pageNum > 1) pageNum--;
                else System.out.println("已是第一页");
            } else {
                System.out.println("无效输入");
            }
        }
        System.out.print("按回车键继续...");
        scanner.nextLine();
    }

    // 管理员工具
    private static void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- 管理员工具 ---");
            System.out.println("1. 重置学生密码");
            System.out.println("2. 返回主菜单");
            int choice = readInt("请选择", 1, 2);
            if (choice == 1) resetStudentPassword();
            else if (choice == 2) back = true;
        }
    }

    private static void resetStudentPassword() {
        System.out.println("（输入0可取消操作）");
        System.out.print("请输入要重置密码的学生学号: ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) {
            System.out.println("已取消重置密码");
            return;
        }

        // 先查询学生是否存在，显示信息供确认
        Student target = studentService.getByStudentId(sid);
        if (target == null) {
            System.out.println("学号不存在，请检查后重试。");
            return;
        }
        System.out.println("即将重置学生：学号 " + target.getStudentId() + "，姓名 " + target.getName());
        System.out.print("确认重置？(y/n): ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消重置");
            return;
        }

        System.out.print("请输入新密码: ");
        String newPwd = scanner.nextLine();
        if (newPwd.equals("0")) {
            System.out.println("已取消重置密码");
            return;
        }

        System.out.print("请再次输入新密码: ");
        String confirmPwd = scanner.nextLine();
        if (confirmPwd.equals("0")) {
            System.out.println("已取消重置密码");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            System.out.println("两次输入的密码不一致，重置失败");
            return;
        }

        try {
            boolean ok = studentService.adminResetPassword(sid, newPwd);
            if (ok) {
                System.out.println("密码重置成功！新密码已生效。");
            } else {
                System.out.println("密码重置失败，请检查学号是否存在。");
            }
        } catch (BusinessException e) {
            System.out.println("重置失败：" + e.getMessage());
        } catch (SystemException e) {
            System.out.println("系统错误，请稍后再试");
            e.printStackTrace();
        }
    }
    // 通用辅助方法
    private static int displayProductList(List<Product> products) {
        if (products == null || products.isEmpty()) { System.out.println("无商品"); return -1; }
        System.out.printf("%-4s %-20s %-10s %-10s%n", "序号", "标题", "价格", "状态");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.printf("%-4d %-20s %-10.2f元 %-10s%n", (i+1), p.getTitle(), p.getPrice(), p.getStatus());
        }
        System.out.print("请输入序号选择商品（0取消）: ");
        int idx = readInt("", 0, products.size());
        return (idx == 0) ? -1 : (idx - 1);
    }

    private static int readInt(String prompt, int min, int max) {
        if (!prompt.isEmpty()) System.out.print(prompt + ": ");
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine());
                if (val >= min && val <= max) return val;
                System.out.print("请输入" + min + "~" + max + "之间的数字: ");
            } catch (NumberFormatException e) {
                System.out.print("非法数字，请重新输入: ");
            }
        }
    }

    private static void exportToCSV(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("没有数据可导出");
            return;
        }
        String fileName = "products_" + System.currentTimeMillis() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write('\uFEFF');
            writer.write("ID,标题,价格,分类,状态,发布时间");
            writer.newLine();
            for (Product p : products) {
                writer.write(escapeCSV(String.valueOf(p.getId())));
                writer.write(',');
                writer.write(escapeCSV(p.getTitle()));
                writer.write(',');
                writer.write(escapeCSV(p.getPrice().toString() + "元"));
                writer.write(',');
                writer.write(escapeCSV(p.getCategory() == null ? "" : p.getCategory()));
                writer.write(',');
                writer.write(escapeCSV(p.getStatus()));
                writer.write(',');
                writer.write(escapeCSV(p.getPublishTime() == null ? "" : p.getPublishTime().toString()));
                writer.newLine();
            }
            System.out.println("导出成功：" + fileName);
        } catch (IOException e) {
            System.out.println("导出失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escapeCSV(String field) {
        if (field == null) return "";
        boolean needQuote = field.contains(",") || field.contains("\"") || field.contains("\n");
        if (needQuote) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}