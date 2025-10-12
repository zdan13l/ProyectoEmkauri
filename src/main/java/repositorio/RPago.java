package repositorio;

import modelo.Pago;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class RPago {

    private final DataSource ds;

    public RPago(DataSource ds) { this.ds = ds; }

    public Pago crear(Pago p) throws SQLException {
        p.validar();
        String sql = "INSERT INTO Pagos (id_compra, monto, metodo, fecha, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setObject(1, p.getIdCompra(), Types.BIGINT);
            st.setBigDecimal(2, p.getMonto());
            st.setString(3, p.getMetodo());
            st.setDate(4, Date.valueOf(p.getFecha()));
            st.setString(5, p.getEstado());
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
        }
        return p;
    }

    public Pago buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, id_compra, monto, metodo, fecha, estado FROM Pagos WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public List<Pago> listar() throws SQLException {
        String sql = "SELECT id, id_compra, monto, metodo, fecha, estado FROM Pagos ORDER BY id DESC";
        List<Pago> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean actualizar(Pago p) throws SQLException {
        p.validar();
        String sql = "UPDATE Pagos SET id_compra=?, monto=?, metodo=?, fecha=?, estado=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            if (p.getIdCompra() == null) st.setNull(1, Types.BIGINT); else st.setLong(1, p.getIdCompra());
            st.setBigDecimal(2, p.getMonto());
            st.setString(3, p.getMetodo());
            st.setDate(4, Date.valueOf(p.getFecha()));
            st.setString(5, p.getEstado());
            st.setLong(6, p.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM Pagos WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Pago map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long idCompra = (Long) rs.getObject("id_compra");
        BigDecimal monto = rs.getBigDecimal("monto");
        String metodo = rs.getString("metodo");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        String estado = rs.getString("estado");
        return new Pago(id, idCompra, monto, metodo, fecha, estado);
    }
}
