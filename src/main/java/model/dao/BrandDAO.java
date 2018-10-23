package main.java.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.model.bean.Brand;
import main.java.model.util.DBUtil;

public class BrandDAO {

	public int getTotal() {
		int total = 0;
		try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {

			String sql = "select count(*) from brand";

			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return total;
	}

	public int add(Brand bean) {
		String sql = "insert into brand values(DEFAULT,?)";
		try (Connection c = DBUtil.getConnection();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, bean.getName());

			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating failed, no rows affected.");
			}

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int id = generatedKeys.getInt(1);
					bean.setId(id);

					return id;
				} else {
					throw new SQLException("Createing failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void update(Brand bean) {

		String sql = "update brand set name= ? where brandId = ?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

			ps.setString(1, bean.getName());
			ps.setInt(2, bean.getId());

			ps.execute();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void delete(int id) {

		try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {

			String sql = "delete from brand where brandId = " + id;

			s.execute(sql);

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public Brand get(int id) {
		Brand bean = null;

		try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {

			String sql = "select * from brand where brandId = " + id;

			ResultSet rs = s.executeQuery(sql);

			if (rs.next()) {
				bean = new Brand();
				String name = rs.getString(2);
				bean.setName(name);
				bean.setId(id);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return bean;
	}

	public List<Brand> list() {
		return list(0, Short.MAX_VALUE);
	}

	public List<Brand> list(int start, int count) {
		List<Brand> beans = new ArrayList<Brand>();

		String sql = "select * from brand order by brandId desc limit ?,? ";

		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

			ps.setInt(1, start);
			ps.setInt(2, count);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Brand bean = new Brand();
				int id = rs.getInt(1);
				String name = rs.getString(2);
				bean.setId(id);
				bean.setName(name);
				beans.add(bean);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return beans;
	}

}
