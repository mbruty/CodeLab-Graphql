package net.bruty.CodeLabs.graphql

import net.bruty.CodeLabs.graphql.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DbUtils {
    companion object {
        fun connect() {
            Database.connect("jdbc:postgresql://pgsql.bruty.net/CodeLabs",
                driver = "org.postgresql.Driver",
                user = "postgres",
                password = "fDpw238ECFRmPnadqHmK1lEP9",
            )
        }

        fun connectTest() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "root", password = "")
        }

        fun createTables() {
            transaction {
                SchemaUtils.create(UsersTable)
                SchemaUtils.create(ModuleTable)
                SchemaUtils.create(LanguageTable)
                SchemaUtils.create(ModuleTaskTable)
                SchemaUtils.create(UserModuleTable)
                SchemaUtils.create(UserTimeLogTable)
                SchemaUtils.create(ProgrammingTaskTable)
                SchemaUtils.create(UserCodeSubmissionTable)
                SchemaUtils.create(ProgrammingTaskStarterCodeTable)
            }
        }

        fun createData() {
            transaction {
                val js = LanguageEntity.new {
                    language = "JavaScript"
                    queueIdentifier = "JS"
                }

                val ts = LanguageEntity.new {
                    language = "TypeScript"
                    queueIdentifier = "TS"
                }

                val dotnet = LanguageEntity.new {
                    language = "C#"
                    queueIdentifier = "DOTNET"
                }

                val doubleTask = ProgrammingTaskEntity.new {
                    defaultLanguage = js
                    description = "For a given number n, return n doubled."
                    title = "Doubling a number"
                }

                val doubleTask_js = ProgrammingTaskStarterCodeEntity.new {
                    starterCode = """
                        function solve(args) {
                          return args;
                        }
                    """.trimIndent()
                    unitTestCode = """
                        import { it, expect } from "bun:test"
                        import solve from ".";

                        it("2 * 2 = 4", () => {
                        	expect(solve(2)).toBe(4);
                        });

                        it("4 * 2 = 8", () => {
                          expect(solve(4)).toBe(8);
                        });
                    """.trimIndent()
                    extendedUnitTestCode = """
                        import { it, expect } from "bun:test"
                        import solve from ".";
                        
                        it("1 * 2 = 2", () => {
                        	expect(solve(1)).toBe(2);
                        });
                        
                        it("2 * 2 = 4", () => {
                        	expect(solve(2)).toBe(4);
                        });
                        
                        it("3 * 2 = 6", () => {
                        	expect(solve(3)).toBe(6);
                        });
                        
                        it("4 * 2 = 8", () => {
                          expect(solve(4)).toBe(8);
                        });
                        
                        it("5 * 2 = 10", () => {
                          expect(solve(5)).toBe(10);
                        });
                    """.trimIndent()
                    task = doubleTask
                    language = js
                }

                val doubleTask_ts = ProgrammingTaskStarterCodeEntity.new {
                    starterCode = """
                        function solve(args: number) {
                          return args;
                        }
                    """.trimIndent()
                    unitTestCode = """
                        import { it, expect } from "bun:test"
                        import solve from ".";

                        it("2 * 2 = 4", () => {
                        	expect(solve(2)).toBe(4);
                        });

                        it("4 * 2 = 8", () => {
                          expect(solve(4)).toBe(8);
                        });
                    """.trimIndent()
                    extendedUnitTestCode = """
                        import { it, expect } from "bun:test"
                        import solve from ".";
                        
                        it("1 * 2 = 2", () => {
                        	expect(solve(1)).toBe(2);
                        });
                        
                        it("2 * 2 = 4", () => {
                        	expect(solve(2)).toBe(4);
                        });
                        
                        it("3 * 2 = 6", () => {
                        	expect(solve(3)).toBe(6);
                        });
                        
                        it("4 * 2 = 8", () => {
                          expect(solve(4)).toBe(8);
                        });
                        
                        it("5 * 2 = 10", () => {
                          expect(solve(5)).toBe(10);
                        });
                    """.trimIndent()
                    task = doubleTask
                    language = ts
                }

                val doubleTask_dotnet = ProgrammingTaskStarterCodeEntity.new {
                    starterCode = """
                        namespace program;
                        public class Solution
                        {
                            public static int Solve(int x)
                            {
                                // Your code here
                                return x;
                            }
                        }
                    """.trimIndent()
                    unitTestCode = """
                        using NUnit.Framework;

                        namespace program;
                        
                        [TestFixture]
                        public class UnitTests
                        {
                            private Solution _solution;
                        
                            [SetUp]
                            public void SetUp()
                            {
                                _solution = new Solution();
                            }
                        
                            [Test]
                            public void TwoTimesTwo_IsFour()
                            {
                                var result = Solution.Solve(2);
                                
                                Assert.AreEqual(4, result);
                            }
                        
                            [Test]
                            public void FourTimesTwo_IsEight() 
                            {
                                var result = Solution.Solve(4);
                                
                                Assert.AreEqual(8, result);
                            }
                        }
                    """.trimIndent()
                    extendedUnitTestCode = """
                        using NUnit.Framework;
                        
                        namespace program;
                        
                        [TestFixture]
                        public class UnitTests
                        {
                            private Solution _solution;
                        
                            [SetUp]
                            public void SetUp()
                            {
                                _solution = new Solution();
                            }
                        
                            [Test]
                            public void TwoTimesTwo_IsFour()
                            {
                                var result = Solution.Solve(2);
                                
                                Assert.AreEqual(4, result);
                            }
                        
                            [Test]
                            public void FourTimesTwo_IsEight() 
                            {
                                var result = Solution.Solve(4);
                                
                                Assert.AreEqual(8, result);
                            }
                        }
                    """.trimIndent()
                    task = doubleTask
                    language = dotnet
                }

                UserEntity.new {
                    username = "demo"
                    email = "demo@gmail.com"
                    password = "password123"
                }
            }
        }

        fun dropTables() {
            transaction {
                SchemaUtils.drop(UserCodeSubmissionTable)
                SchemaUtils.drop(ProgrammingTaskStarterCodeTable)
                SchemaUtils.drop(ProgrammingTaskTable)
                SchemaUtils.drop(UserModuleTable)
                SchemaUtils.drop(ModuleTaskTable)
                SchemaUtils.drop(LanguageTable)
                SchemaUtils.drop(ModuleTable)
                SchemaUtils.drop(UsersTable)
            }
        }
    }
}