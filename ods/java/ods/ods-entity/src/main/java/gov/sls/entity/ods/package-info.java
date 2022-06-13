/**
 * 代表資料表一筆或一組資料的物件類別。
 * <p>
 * Package 名稱以 {@code gov.sls.entity} 開頭，因為各 Entity 應該是跨系統使用的，不要放在各系統
 * Package 中。　
 * <p>
 * <strong>實作注意:</strong> 統一使用 Java Persistence API 2 之 Entity 格式，複合 Primary Key
 * 類別名稱統一以 {@code Id} 結尾，主 Entity 以 {@link javax.persistence.IdClass} 方式參照。
 */
package gov.sls.entity.ods;
