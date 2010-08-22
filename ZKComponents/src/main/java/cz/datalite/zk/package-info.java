/*
 * <p>ZK framework extension library.</p>
 *
 * <p>
 * This library was created as an extension to current ZK components (as of version 3.5).
 * Original components for multiple objects from database are too much UI-centric and require lot of boilerplate.
 * We have added additional layer to the component to support MVC (Model View Controller) design pattern, which allowed us to
 * handle data access operations in uniform style in controller, regardless of actual data access implementation. Our components do
 * what original ZK components do (and of course more), but what ZK component does in memory, we streamlined to the controller and
 * allow the developer to do it in the database. We provide some persistence-specific implementations as well, mainly for Hibernate, where
 * everything is handled with generic DAO without any coding at all.
 * <p>
 *
 * Main MVC components:
 * <ul>
 *   <li>Listbox - most advanced component. Supports database filtering, database sorting, database paging, excel export, ... all with minimum implementation.</li>
 *   <li>Combobox - same MVC controller design as listbox. It loads data only after the user opens the combo (page is loaded much more faster).</li>
 *   <li>Lovbox - based on Bandbox and Listbox. Because Combobox loads all data at once (suggestion is not very usable yet),
 *            selection from large list of data can be done from listbox with filter in bandpopup).
 *   <li>Grid - not implemented yet. Should be similar implementation to listbox.
 * </ul>
 * There are some other minor improvements to other components as well.
 *
 * <p>Other feature of combobox/lovbox component is that thay can be connected to cascade (e.g. select country and then list of towns should refresh itself to show only towns from selected country)</p>
 *
 * <p>There is
 */
package cz.datalite.zk;

