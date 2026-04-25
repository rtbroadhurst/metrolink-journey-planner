  package routing;                                                                                                                                                 
                                                                                                                                                                   
  import model.Station;                                                                                                                                            
                                                                                                                                                                 
  public interface Router {                                                                                                                                        
      Route findRoute(Station from, Station to);                                                                                                                 
  }