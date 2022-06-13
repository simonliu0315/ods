/**
 * Service 層，業務邏輯主要在這個層級處理，理應是整個應用程式中最複雜最有價值的東西。
 * <p>
 * Service 可以再重覆利用其他的 Service，所以最好切割重可以重複利用的功能元件;
 * 需要透露給 Presentation 層的東西最好能 Composite 簡化，以表達一完整業務操作為原則。
 * <p>
 * <strong>實作注意:</strong> 不用特別區分 Interface 與 Implementation，除非有特別的需求。
 * 目前的 Mockito 也能支援 Concrete Class 的 Mock，不用擔心。
 */
package gov.sls.ods.service;
