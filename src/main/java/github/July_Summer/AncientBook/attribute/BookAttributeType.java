package github.July_Summer.AncientBook.attribute;

public enum BookAttributeType {

      PST_DAMAGE("PstDamage"), 
      ARMOR("Armor"), 
      SUNDER_CHANCE("SunderChance"), 
      REAL_DAMAGE("RealDamage"), 
      PVP_DAMAGE("PVPDamage"),
      PVE_DAMAGE("PVEDamage"), 
      SHOOT_DAMAGE("ShootDamage"), 
      CRIT_CHANCE("CritChance"), 
      CRIT_DAMAGE("CritDamage"),
      HEALTH_CHANCE("HealthChance"),
      HEALTH_STEAL("HealthSteal"),
      DODGE_CHANCE("DodgeChance"),
      HIT_CHANCE("HitChance"),
      KILL_CHANCE("KillChance"),
      THORNS_DAMAGE("ThornsDamage"),
      SPARK_SITE("SparkSite"),
      USE_PERMISSION("UsePermission");
      
     
      private String vaule;
      
      public String getVaule() {
          return vaule;
      }
      
      private BookAttributeType(String vaule) {
          this.vaule = vaule;
      }
      
      public boolean isSpecial()
      {
          return vaule.equals("SparkSite") || vaule.equals("UsePermission");
      }
      
      
}
