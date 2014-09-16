/**
 * Example:
 * <h1>Converter holder:</h1>
 * <pre>
 *     &#64;org.springframework.stereotype.Component("conv")
 *     public class Converters {
 *         private DurationConverter duration = new DurationConverter();
 *         public DurationConverter getDuration() {
 *             return duration;
 *         }
 *     }
 * </pre>
 * <h1>ZUL:</h1>
 * <pre>
 *     &lt;listcell label="@load(each.duration) @converter(conv.duration)"/&gt;
 * </pre>
 */
package cz.datalite.zk.converter;