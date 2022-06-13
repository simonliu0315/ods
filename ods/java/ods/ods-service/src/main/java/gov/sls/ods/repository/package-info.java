/**
 * Repository 層，提供存取後端資料庫之功能。
 * <p>
 * 所有的 Query String 都應該包裝在這一層，前端程式知道越少底層的東西越好。
 * <p>
 * <strong>實作注意:</strong> Custom Interface 及實作 Class 不用特別區分目錄，只要將其 Visibility
 * 設 Default 等級 (不要有 {@code public}, {@code protected} 或 {@code private} 等關鍵字)，讓別的
 * Package 無法存取就好。
 */
package gov.sls.ods.repository;
