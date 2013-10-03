package cz.datalite.dao.plsql;


import javax.sql.DataSource;

/**
 * Interface pro vytvoření spouštěče DB procedur
 *
 * Volání DB funkce:
 *
 *  class CallDbFunction
 *  {
 *      @Autowired
 *      StoredProcedureInvokerCreator storedProcedureInvokerCreator ;
 *
 *
 *      public void call()
 *      {
 *          StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *          invoker.execute() ;
 *      }
 *
 *      public Integer callWithResult()
 *      {
 *          StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *          invoker.declareReturnParameter( Types.NUMERIC ) ;
 *
 *          return invoker.getResult( Integer.class ) ;
 *      }
 *
 *      public void callWithInput()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *              invoker.setParameter( "prvni", Types.NUMERIC, 10l ) ;
 *              invoker.setParameter("druhej", Types.VARCHAR, "10") ;
 *
 *              invoker.execute() ;
 *      }
 *
 *      public Date callWithOutput()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *              invoker.setParameter( "prvni", Types.NUMERIC, 10l ) ;
 *              invoker.setParameter( "druhej", Types.VARCHAR, "10" ) ;
 *              invoker.declareOutParameter( "vystupni", Types.DATE ) ;
 *
 *              Map<String, Object> values = invoker.execute() ;
 *
 *              return (Date) values.get( "vystupni" );
 *      }
 *
 *      public Date callWithInputOutput()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *              invoker.setParameter( "prvni", Types.NUMERIC, 10l ) ;
 *              invoker.setParameter( "druhej", Types.VARCHAR, "10" ) ;
 *              invoker.declareInOutParameter( "vystupni", Types.DATE, new Date() ) ;
 *
 *              Map<String, Object> values = invoker.execute() ;
 *
 *              return (Date) values.get( "vystupni" );
 *      }
 *
 *      public String callWithInputAndOutputAndResult()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.COMPILE" ) ;
 *
 *              invoker.declareReturnParameter( Types.VARCHAR ) ;
 *
 *              invoker.setParameter( "prvni", Types.NUMERIC, 10l ) ;
 *              invoker.setParameter( "druhej", Types.VARCHAR, "10" ) ;
 *              invoker.declareInOutParameter( "vystupni", Types.DATE, new Date() ) ;
 *
 *              Map<String, Object> values = invoker.execute() ;
 *
 *              Date date = (Date) values.get( "vystupni" );
 *
 *              return (String)values.get( StoredProcedureInvoker.RETURN_VALUE_NAME ) ;
 *      }
 * *
 *      public void callWithDatabaseArray()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.setLetter" ) ;
 *              List<StructConvertableImpl> seznam;
 *
 *              invoker.setParameter( "prvni", "NXT.TABLE_VARCHAR", seznam ) ;
 *
 *              invoker.execute() ;
 *      }

 *      public void callWithDatabaseObject()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.setLetter" ) ;
                StructConvertableImpl seznam;
 *
 *              invoker.setParameter( "prvni", "NXT.STRUKTURA", seznam ) ;
 *
 *              invoker.execute() ;
 *      }
 *
 *      public E callWithReturnStructure()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.getLetter" ) ;
 *              E seznam;
 *
 *              invoker.declareReturnStructParameter( "NXT.MUJ_ZNAK" ) ;
 *
 *              seznam = invoker.getResult( <typ vysledku> ) ;
 *
 *      }
 *
 *      public List<E> callWithReturnArray()
 *      {
 *              StoredProcedureInvoker invoker = storedProcedureInvokerCreator.create( "nxt.getLetter" ) ;
 *              List<E> seznam;
 *
 *              invoker.declareReturnArrayParameter( "NXT.MOJE_POLE_ZNAKU" ) ;
 *
 *              seznam = invoker.getResultList( <Typ polozky seznamu> ) ;
 *      }
 *  }
 *
 */
@SuppressWarnings("JavaDoc")
public interface StoredProcedureInvokerCreator
{
    /**
     * @return spoustec ulozenych procedur
     */
    StoredProcedureInvoker create() ;

    /**
     * @param name       jmeno spoustene funkce
     * @return spoustec ulozenych procedur
     */
    StoredProcedureInvoker create(String name) ;

    /**
     * @param dataSource        zdroj data
     * @return spoustec ulozenych procedur
     */
    StoredProcedureInvoker create(DataSource dataSource) ;

    /**
     * @param dataSource        zdroj data
     * @param name              jmeno spoustene funkce
     *
     * @return spoustec ulozenych procedur
     */
    StoredProcedureInvoker create(DataSource dataSource, String name) ;

    /**
     * @param dataSource        zdroj data
     * @param name              jmeno spoustene funkce
     * @param resultType        navratovy typ funkce
     *
     * @return spoustec ulozenych procedur
     */
    StoredProcedureInvoker create(DataSource dataSource, String name, int resultType) ;
}
